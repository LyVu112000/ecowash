package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.LaundryForm;
import vuly.thesis.ecowash.core.repository.core.ILaundryFormRepository;

@Service
public class LaundryFormService {

    @Autowired
    ILaundryFormRepository laundryFormRepository;

    public LaundryFormService(ILaundryFormRepository laundryFormRepository) {
        this.laundryFormRepository = laundryFormRepository;
    }

    public LaundryForm getByValue(String value) {
        return laundryFormRepository.findByValue(value).orElse(null);
    }
}
