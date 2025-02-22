package tz.business.eCard.utils.userExtractor;
import tz.business.eCard.models.UserAccount;

public interface LoggedUser {

    UserInfo getInfo() ;

    UserAccount getUserAccount() ;
}
