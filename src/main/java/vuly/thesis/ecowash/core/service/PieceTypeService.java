package vuly.thesis.ecowash.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.PieceType;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.repository.core.IPieceTypeRepository;

import java.util.Optional;

@Service
public class PieceTypeService {

    @Autowired
    private final IPieceTypeRepository pieceTypeRepository;

    public PieceTypeService(IPieceTypeRepository pieceTypeRepository) {
        this.pieceTypeRepository = pieceTypeRepository;
    }



    public PieceType getByValue(String value) {
        Optional<PieceType> optional = pieceTypeRepository.findByValue(value);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new AppException(4041);
        }
    }
}
