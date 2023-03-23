package com.cg.api;

import com.cg.exception.EmailExistsException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.model.Customer;
import com.cg.model.CustomerAvatar;
import com.cg.model.Deposit;
import com.cg.model.LocationRegion;
import com.cg.model.dto.*;
import com.cg.service.customer.ICustomerService;
import com.cg.service.customerAvatar.ICustomerAvatarService;
import com.cg.service.uploadMedia.UploadService;
import com.cg.utils.AppUtils;
import com.cg.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ICustomerAvatarService customerAvatarService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private UploadUtils uploadUtils;

    @Autowired
    private UploadService uploadService;


    @GetMapping
    public ResponseEntity<?> getAll() {

//        List<Customer> customers = customerService.findAll();

        List<CustomerResDTO> customerResDTOS = customerService.findAllByDeletedIsFalse();
        List<CustomerDTO> customerDTOS = new ArrayList<>();
//
        for (CustomerResDTO item : customerResDTOS) {
            CustomerAvatarDTO customerAvatarDTO = new CustomerAvatarDTO();
            customerAvatarDTO.setId(item.getAvatarId());
            customerAvatarDTO.setFileFolder(item.getFileFolder());
            customerAvatarDTO.setFileName(item.getFileName());
            customerAvatarDTO.setFileUrl(item.getFileUrl());
            CustomerDTO customerDTO = item.toCustomerDTO(customerAvatarDTO);
            customerDTOS.add(customerDTO);
        }

        return new ResponseEntity<>(customerDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {

//        Optional<Customer> customerOptional = customerService.findById(customerId);
        Optional<CustomerResDTO> customerResDTO = customerService.findCustomerResDTOById(customerId);

        if (!customerResDTO.isPresent()) {
            throw new ResourceNotFoundException("Customer not valid");
        }

        CustomerAvatarDTO customerAvatarDTO = new CustomerAvatarDTO();
        customerAvatarDTO.setId(customerResDTO.get().getAvatarId());
        customerAvatarDTO.setFileFolder(customerResDTO.get().getFileFolder());
        customerAvatarDTO.setFileName(customerResDTO.get().getFileName());
        customerAvatarDTO.setFileUrl(customerResDTO.get().getFileUrl());

//        Customer customer = customerOptional.get();
        CustomerDTO customerDTO = customerResDTO.get().toCustomerDTO(customerAvatarDTO);

        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {

        new CustomerDTO().validate(customerDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Boolean existEmail = customerService.existsByEmailEquals(customerDTO.getEmail());

        if (existEmail) {
            throw new EmailExistsException("The email is exists");
        }

        customerDTO.setId(null);
        customerDTO.setBalance(BigDecimal.ZERO);
        customerDTO.getLocationRegion().setId(null);

        Customer customer = customerDTO.toCustomer();
        customer = customerService.save(customer);

        customerDTO = customer.toCustomerDTO();

        return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
    }


    @PostMapping("/create-with-avatar")
    public ResponseEntity<?> createWithAvatar(CustomerCreateAvatarReqDTO customerCreateAvatarReqDTO) {

        MultipartFile file = customerCreateAvatarReqDTO.getFile();

        LocationRegionDTO locationRegionDTO = customerCreateAvatarReqDTO.toLocationRegionDTO();
        CustomerDTO customerDTO = customerCreateAvatarReqDTO.toCustomerDTO(locationRegionDTO);

        Boolean existEmail = customerService.existsByEmailEquals(customerDTO.getEmail());

        if (existEmail) {
            throw new EmailExistsException("The email is exists");
        }

        if (file != null) {
            Customer customer = customerDTO.toCustomer();
            CustomerCreateAvatarResDTO customerCreateAvatarResDTO = customerService.createWithAvatar(customer, file);

            return new ResponseEntity<>(customerCreateAvatarResDTO, HttpStatus.CREATED);
        }
        else {
            customerDTO.setId(null);
            customerDTO.setBalance(BigDecimal.ZERO);
            customerDTO.getLocationRegion().setId(null);

            Customer customer = customerDTO.toCustomer();
            customer = customerService.save(customer);

            customerDTO = customer.toCustomerDTO();

            return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/delete-avatar/{customerId}")
    public ResponseEntity<?> deleteAvatar(@PathVariable Long customerId) throws IOException {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer invalid");
        }

        Optional<CustomerAvatar> customerAvatar = customerAvatarService.findByCustomer(customerOptional.get());

        String publicId = customerAvatar.get().getCloudId();


        uploadService.destroyImage(publicId, uploadUtils.buildImageUploadParams(customerAvatar.get()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{customerId}")
    public ResponseEntity<?> update(@PathVariable Long customerId, @Validated @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found");
        }

        new CustomerDTO().validate(customerDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Customer customer = customerDTO.toCustomer();
        customer.setId(customerId);
        customerService.save(customer);

        return new ResponseEntity<>(customer.toCustomerDTO(), HttpStatus.OK);
    }

    @PatchMapping("/update-with-avatar/{customerId}")
    public ResponseEntity<?> updateWithAvatar(@PathVariable Long customerId, MultipartFile file, CustomerUpdateReqDTO customerUpdateReqDTO, BindingResult bindingResult) throws IOException {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found");
        }

        if (file == null) {
            LocationRegionDTO locationRegionDTO = customerUpdateReqDTO.toLocationRegionDTO();

            CustomerDTO customerDTO = customerUpdateReqDTO.toCustomerDTO(locationRegionDTO);
            Customer customer = customerDTO.toCustomer();
            customer.setId(customerId);

            CustomerUpdateAvatarResDTO customerUpdateAvatarResDTO = customerService.update(customer);
            return new ResponseEntity<>(customerUpdateAvatarResDTO, HttpStatus.OK);
        }
        else {
            LocationRegionDTO locationRegionDTO = customerUpdateReqDTO.toLocationRegionDTO();

            CustomerDTO customerDTO = customerUpdateReqDTO.toCustomerDTO(locationRegionDTO);
            Customer customer = customerDTO.toCustomer();
            customer.setId(customerId);

            CustomerUpdateAvatarResDTO customerUpdateAvatarResDTO = customerService.updateWithAvatar(customer, file);

            return new ResponseEntity<>(customerUpdateAvatarResDTO, HttpStatus.OK);
        }
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<?> deposit(@PathVariable Long customerId, @RequestBody DepositDTO depositDTO) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Customer customer = customerOptional.get();

//        BigDecimal currentBalance = customer.getBalance();
        BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(depositDTO.getTransactionAmount()));
//        BigDecimal newBalance = currentBalance.add(transactionAmount);
//        customer.setBalance(newBalance);

        Deposit deposit = new Deposit();
        deposit.setTransactionAmount(transactionAmount);
        deposit.setCustomer(customer);

        customerService.deposit(deposit);

        return new ResponseEntity<>(customer.toCustomerDTO(), HttpStatus.OK);
    }

}
