package tz.business.eCard.utils.userExtractor;
import tz.business.eCard.models.Account;

public interface LoggedUser {

    UserInfo getInfo() ;

    Account getUserAccount() ;
}
