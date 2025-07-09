package tz.business.eCard.ServiceImpls;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tz.business.eCard.dtos.AccountDto;
import tz.business.eCard.dtos.BioDto;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.AccountRepository;
import tz.business.eCard.services.FileStorageService;
import tz.business.eCard.services.AccountService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.UserType;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AccountsServiceImpl implements AccountService {
    private Logger log = Logger.getLogger(AccountsServiceImpl.class.getName());

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FileStorageService fileStorageService;
    private Account account;
    @Autowired
    private LoggedUser loggedUser;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${eCard.upload.max-file-size:5242880}")
    private String uploadDir;

    @Value("${eCard.upload.allowed-image-types:jpg,jpeg,png,gif}")
    private String allowedImageTypes;
    @Value("${eCard.upload.max-image-width:1920}")
    private int maxWidth;
    @Value("${eCard.upload.max-image-height:1080}")
    private int maxHeight;
    @Value("${eCard.upload.max-image-size:5242880}")
    private long MAX_IMAGE_SIZE;
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    @Override
    public Response<Account> createUpdateUserAccount(AccountDto accountDto) {
        try {
            Account user = loggedUser.getUserAccount();

            if (user == null) {
                log.warning("UNAUTHORIZED USER CREATING USER");
                return  new Response<>(true , ResponseCode.UNAUTHORIZED,"Full Authentication required");
            }

            Optional<Account> accountOptional = accountRepository.findFirstByUserName(accountDto.getUsername());
            Optional<Account> accountOptional1 = accountRepository.findFirstByPhoneNumber(accountDto.getPhoneNumber());

            if (accountOptional.isPresent()) {
                return  new Response<>(true , ResponseCode.DUPLICATE_KEY , "UserName Already Exists");
            }

            if (accountOptional1.isPresent()) {
                return  new Response<>(true , ResponseCode.DUPLICATE_KEY , "PhoneNumber Already Exists");
            }

            if (accountDto.getPassword() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "Password must not be null");
            }

            if(accountDto.getFirstName() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "FirstName must not be null");
            }

            if(accountDto.getLastName() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "LastName must not be null");
            }

            if(accountDto.getPhoneNumber() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "PhoneNumber must not be null");
            }

            if (accountDto.getEmail() == null) {
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "Email must not be null");
            }

            if (accountDto.getCompanyTitle() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "CompanyTitle must not be null");
            }

            if(accountDto.getJobTitle() == null) {
                return  new Response<>(true , ResponseCode.NULL_ARGUMENT , "JobTitle must not be null");
            }

            if(accountDto.getMiddleName()==null) {
                accountDto.setMiddleName("");
            }

            else{
                if(!accountDto.getMiddleName().isBlank() && !Objects.equals(accountDto.getMiddleName(), accountDto.getMiddleName())) {
                    accountDto.setMiddleName(accountDto.getMiddleName());
                }
                if(!accountDto.getLastName().isBlank() && !Objects.equals(accountDto.getLastName(), accountDto.getLastName())) {
                    accountDto.setLastName(accountDto.getLastName());
                }

                if(!accountDto.getFirstName().isBlank() && !Objects.equals(accountDto.getFirstName(), accountDto.getFirstName())) {
                    accountDto.setFirstName(accountDto.getFirstName());
                }

                if(!accountDto.getPassword().isBlank() && !Objects.equals(accountDto.getPassword(), accountDto.getPassword())) {
                    accountDto.setPassword(accountDto.getPassword());
                }
                if(!accountDto.getJobTitle().isBlank() && !Objects.equals(accountDto.getJobTitle(), accountDto.getJobTitle())) {
                    accountDto.setJobTitle(accountDto.getJobTitle());
                }
                if(!accountDto.getCompanyTitle().isBlank() && !Objects.equals(accountDto.getCompanyTitle(), accountDto.getCompanyTitle())) {
                    accountDto.setCompanyTitle(accountDto.getCompanyTitle());
                }
                if(!accountDto.getPhoneNumber().isBlank() && !Objects.equals(accountDto.getPhoneNumber(), accountDto.getPhoneNumber())) {
                    accountDto.setPhoneNumber(accountDto.getPhoneNumber());
                }
                if(!accountDto.getPassword().isBlank() && !Objects.equals(accountDto.getPassword(), accountDto.getPassword())) {
                    accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
                }
                if(accountDto.getUserRole() == null){
                    account.setUserType(String.valueOf(UserType.CUSTOMER));
                }
                else if(accountDto.getUserRole().equalsIgnoreCase(UserType.ADMIN.name()))
                    account.setUserType(String.valueOf(UserType.ADMIN));
                else if (accountDto.getUserRole().equalsIgnoreCase(UserType.SUPER_ADMIN.name()))
                    account.setUserType(String.valueOf(UserType.SUPER_ADMIN));
                else if (accountDto.getUserRole().equalsIgnoreCase(UserType.SELLER.name()))
                    account.setUserType(String.valueOf(UserType.SELLER));
                else if(accountDto.getUserRole().equalsIgnoreCase(UserType.VENDOR.name()))
                    account.setUserType(String.valueOf(UserType.VENDOR));
                else account.setUserType(String.valueOf(UserType.CUSTOMER));

                Account savedUser = accountRepository.save(account);
                return new Response<>(false,ResponseCode.SUCCESS,savedUser);
            }
        } catch (Exception e){
            log.warning(e.getMessage());
        }

        return new Response<>(false , ResponseCode.UNAUTHORIZED , "Unauthorized");
    }

    @Override
    public Response<Account> deleteUserAccount(String uuid) {

        try {
            Account user = loggedUser.getUserAccount();

            if(user == null) {
                log.warning("UNAUTHORIZED USER DELETING USER");
                return  new Response<>(true , ResponseCode.UNAUTHORIZED , "Full Authentication required");
            }
            Optional<Account> userAccount = accountRepository.findFirstByUuid(uuid);
            if(userAccount.isPresent()) {
                accountRepository.delete(userAccount.get());
                return new Response<>(false, ResponseCode.SUCCESS, "User Account Deleted");
            } else {
                return new Response<>(true, ResponseCode.NOT_FOUND, "No user found with id " + uuid);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new Response<>(true, ResponseCode.UNAUTHORIZED , "Unauthorized");
    }

    @Override
    public Response<Account> getUserByUuid(String uuid) {
        try {
            Optional<Account> userAccount = accountRepository.findFirstByUuid(uuid);
            return userAccount.map(account -> new Response<>(true, ResponseCode.SUCCESS, "User with id " + uuid + " found", account)).orElseGet(() -> new Response<>(true, ResponseCode.NOT_FOUND, "No user found with id " + uuid));

        } catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST , "Bad Request");
    }

    @Override
    public Page<Account> getAllUserAccounts(Pageable pageable) {
        try {
          return accountRepository.findALlByDeletedFalse(pageable);

        }catch (Exception e){
            log.warning(e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<Account> getOfficials(Pageable pageable) {
        try{
            return accountRepository.findAllByUserTypeNot(String.valueOf(UserType.CUSTOMER), pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<Account> getCustomers(Pageable pageable) {
        try{
            return accountRepository.findAllByUserTypeAndDeletedFalse(String.valueOf(UserType.CUSTOMER), pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<Account> getVendors(Pageable pageable) {
        try {
            return accountRepository.findAllByUserTypeAndDeletedFalse(String.valueOf(UserType.VENDOR), pageable);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Response<Account> updateBio(BioDto bioDto) {
        try{
            if(bioDto.getBio() == null){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "Bio is null");
            }
            if(bioDto.getUserUuid() == null){
                return new Response<>(true , ResponseCode.NULL_ARGUMENT , "User UUID is null");
            }

            Optional<Account>accountOptional = accountRepository.findFirstByUuid(bioDto.getUserUuid());

            if(accountOptional.isPresent()) {
                return new Response<>(true, ResponseCode.ALREADY_EXISTS, "User with "+bioDto.getUserUuid() + " already exists");
            }
            Account account = accountOptional.get();
            account.setBiography(bioDto.getBio());
            account.setPublishBio(bioDto.isPublishBio());
            Account savedUser = accountRepository.save(account);
            return new Response<>(true, ResponseCode.SUCCESS, "User with "+bioDto.getUserUuid() + " updated", savedUser);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST , "Bad Request");
    }
    
    @Override
    public Response<Map<String, Object>> createUpdateProfilePhoto(String uuid, MultipartFile file) {
        try {

            validateFile(file);

            if (uuid == null || uuid.isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "User UUID is required");
            }

            Account user = loggedUser.getUserAccount();
            if (user == null) {
                return new Response<>(true, ResponseCode.UNAUTHORIZED, "Full Authentication required");
            }

            Optional<Account> accountOptional = accountRepository.findFirstByUuid(uuid);
            if (accountOptional.isEmpty()) {
                return new Response<>(true, ResponseCode.NOT_FOUND, "No User found with this id");
            }

            if (file == null || file.isEmpty()) {
                return new Response<>(true, ResponseCode.NULL_ARGUMENT, "Profile photo file is required");
            }

            String fileName = generateFileName(file.getOriginalFilename());
            byte[] processedImageBytes = processImage(file);
            String savedFilePath = fileStorageService.saveFile(processedImageBytes , fileName , uploadDir);

            Account account = accountOptional.get();
            account.setProfilePhoto(file.getOriginalFilename());
            accountRepository.save(account);

            return new Response<>(false, ResponseCode.SUCCESS, "Profile photo updated successfully");

        } catch (Exception e) {
            log.warning("Error updating profile photo: " + e.getMessage());
            return new Response<>(true, ResponseCode.BAD_REQUEST, "Failed to update profile photo");
        }
    }

    private Response<?> validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()){
            log.warning("File is null or empty");
            return  new Response<>(true, ResponseCode.BAD_REQUEST, "File is null or empty");
        }

        assert file != null;
        if(file.getSize() > MAX_IMAGE_SIZE){
            log.warning("File size is greater than 5MB");
            return  new Response<>(true, ResponseCode.BAD_REQUEST, "File size is greater than 5MB");
        }

        String contentType = file.getContentType();
        if(contentType == null || contentType.isEmpty() || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())){
            log.warning("File content type is null or empty");
            return  new Response<>(true, ResponseCode.BAD_REQUEST, "File content type is null or empty or Unsupported file type");
        }

        String originalFileName = file.getOriginalFilename();
        if(originalFileName == null || originalFileName.isEmpty() || !hasValidImageExtensions(originalFileName)){
            log.warning("File original name is null or empty");
            return  new Response<>(true, ResponseCode.BAD_REQUEST, "File original name is null or empty or Invalid Extension Type");
        }

        try{
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if(image == null){
                return  new Response<>(true, ResponseCode.BAD_REQUEST, "File is not an image");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Response<>(false, ResponseCode.SUCCESS, "File is valid");
    }

    private boolean hasValidImageExtensions(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        List<String> allowedFileExtensions = Arrays.asList(allowedImageTypes.split(","));
        return  allowedFileExtensions.contains(extension);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return  lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    private byte[] processImage(MultipartFile file) throws IOException {
        BufferedImage originalImage =  ImageIO.read(new ByteArrayInputStream(file.getBytes()));

        BufferedImage resizedImage = resizeImage(originalImage , maxWidth , maxHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getImageFormat(file.getContentType());
        ImageIO.write(resizedImage , formatName , baos);
        return baos.toByteArray();
    }

    private BufferedImage resizeImage(BufferedImage originalImage , int maxWidth , int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if(originalWidth <= maxWidth && originalHeight <= maxHeight){
            return  originalImage;
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Improve image quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    private String getImageFormat(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
                return "jpeg";
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            default:
                return "jpg"; // Default fallback
        }
    }

    private String generateFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + fileExtension;
    }

    private void deleteExistingPhoto(String photoPath){
        try{
            fileStorageService.deleteFile(photoPath);
        } catch (Exception e) {
            log.warning("Error deleting existing photo: " + photoPath + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
