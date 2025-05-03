package tn.esprit.examen.Smartmeet.Services.MaryemSalhi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.Smartmeet.entities.MaryemSalhi.MentalHealth;
import tn.esprit.examen.Smartmeet.entities.Users.Users;
import tn.esprit.examen.Smartmeet.repositories.MaryemSalhi.IMentalhealthRepository;
import tn.esprit.examen.Smartmeet.repositories.Users.UserRepository;
import tn.esprit.examen.Smartmeet.security.services.UserDetailsImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentalHealthServicesImpl implements IMentalHealthServices {
    @Autowired
    private final IMentalhealthRepository mentalhealthRepository;
    private final UserRepository userRepository;


    @Override
    public MentalHealth addMentalhealth(MentalHealth mentalhealth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Optional<Users> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            log.error("Utilisateur avec le nom d'utilisateur {} non trouvé", username);
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        Users user = userOptional.get();
        mentalhealth.setUser(user);
        return mentalhealthRepository.save(mentalhealth);
    }

    @Override
    public MentalHealth updateMentalhealth(MentalHealth mentalhealth) {
        log.info("Updating mental health with ID: {}", mentalhealth.getIdMentalHealth());
        Optional<MentalHealth> existingMentalHealth = mentalhealthRepository.findById(mentalhealth.getIdMentalHealth());
        if (existingMentalHealth.isPresent()) {
            MentalHealth current = existingMentalHealth.get();
            current.setResponseMoment(mentalhealth.getResponseMoment());
            current.setStressLevel(mentalhealth.getStressLevel());
            current.setEmotionalState(mentalhealth.getEmotionalState());
            current.setSupportNeed(mentalhealth.getSupportNeed());
            return mentalhealthRepository.save(current);
        } else {
            log.warn("MentalHealth with ID: {} not found", mentalhealth.getIdMentalHealth());
            throw new IllegalArgumentException("MentalHealth with ID " + mentalhealth.getIdMentalHealth() + " not found");
        }
    }

    @Override
    public void deleteMentalhealth(Long id) {
        mentalhealthRepository.deleteById(id);
    }

    @Override
    public MentalHealth getMentalhealthById(Long id) {
        return mentalhealthRepository.findById(id).orElse(null);
    }

    @Override
    public List<MentalHealth> getAllMentalhealths() {
        return mentalhealthRepository.findAll();
    }
    @Override
    public List<MentalHealth> getAllMentalhealthsUsers() {
        return mentalhealthRepository.findAllMentalHealthWithUser();
    }
    @Override
    public MentalHealth addMentalHealthForCurrentUser(MentalHealth mentalHealth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Users> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        Users user = userOptional.get();
        mentalHealth.setUser(user);
        return mentalhealthRepository.save(mentalHealth);
    }

    @Override
    public byte[] generateQRCode(Long mentalHealthId, int width, int height) throws WriterException, IOException {
        MentalHealth mentalHealth = getMentalhealthById(mentalHealthId);
        if (mentalHealth == null) {
            throw new IllegalArgumentException("Enregistrement de santé mentale non trouvé avec l'ID : " + mentalHealthId);
        }
        String mentalHealthDetails = String.format(
                "Mental Health ID: %d\nResponse Moment: %s\nStress Level: %d\nEmotional State: %s\nSupport Need: %s\nSubmission Date: %s",
                mentalHealth.getIdMentalHealth(),
                mentalHealth.getResponseMoment().toString(),
                mentalHealth.getStressLevel(),
                mentalHealth.getEmotionalState().toString(),
                mentalHealth.getSupportNeed().toString(),
                mentalHealth.getSubmissionDate().toString()
        );
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(mentalHealthDetails, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    @Override
    public List<MentalHealth> getLastThreeSubmissionsByUser(Long userId) {
        return mentalhealthRepository.findTop3ByUserUserIDOrderBySubmissionDateDesc(userId);
    }

    public List<MentalHealth> getMentalHealthsByCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Optional<Users> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            log.error("Utilisateur avec le nom d'utilisateur {} non trouvé", username);
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        Users user = userOptional.get();
        return mentalhealthRepository.findByUserUserID(user.getUserID());
    }
}