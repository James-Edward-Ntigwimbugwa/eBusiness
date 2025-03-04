package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.DashboardDto;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.repositories.MyCardRepository;
import tz.business.eCard.services.DashboardService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.UserType;
import tz.business.eCard.utils.userExtractor.LoggedUser;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private LoggedUser loggedUser;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private MyCardRepository myCardRepository;

    @Override
    public Response<DashboardDto> getDashboardData() {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if (user == null) {
                return new Response<>(true,ResponseCode.UNAUTHORIZED, "Unauthorized access");
            }
            DashboardDto dashboardDto = new DashboardDto();
            if(user.getUserType().equals(UserType.CUSTOMER.toString())){
                Long myCards =  cardRepository.countAllByUserUuid(user);
                dashboardDto.setMyCards(myCards);

                Long myContacts = myCardRepository.countByUserIdAndDeletedFalse(user);
                dashboardDto.setMyContacts(myContacts);

                Long myFavorites = myCardRepository.countMyCardsByUserIdAndFavouritesTrueAndDeletedFalse(user);
                dashboardDto.setMyFavoritesCards(myFavorites);

                return new Response<>(false , ResponseCode.SUCCESS, "Data Found" , dashboardDto);
            } else if (user.getUserType().equals(UserType.ADMIN.toString())) {
                Long myCards = cardRepository.countAllByUserUuid(user);
                dashboardDto.setMyCards(myCards);

                Long myContacts = myCardRepository.countByUserIdAndDeletedFalse(user);
                dashboardDto.setMyFavoritesCards(myContacts);

                Long myFavorites = myCardRepository.countByUserIdAndDeletedFalse(user);
                dashboardDto.setMyFavoritesCards(myFavorites);

                Long activeCards = cardRepository.countAllByDeletedFalse();
                dashboardDto.setActiveCards(activeCards);

                return new Response<>(false, ResponseCode.SUCCESS, "Data Fetched" , dashboardDto);
            }

        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new Response<>(true, ResponseCode.BAD_REQUEST,"Unknown Error occurred");
    }
}
