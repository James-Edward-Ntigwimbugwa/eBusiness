package tz.business.eCard.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.business.eCard.dtos.GroupDto;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.utils.Response;

import java.util.List;

public interface GroupService {
    Response<CardGroup> createUpdateGroup(GroupDto groupDto);
    Response<CardGroup> deleteGroup(String groupUuid);
    Response<CardGroup> getGroup(String groupUuid);
    Page<CardGroup> getAllUserGroups(Pageable pageable);
    List<CardGroup> getGroupCards(String groupUuid);
}
