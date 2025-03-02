package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.business.eCard.dtos.GroupDto;
import tz.business.eCard.models.CardGroup;
import tz.business.eCard.models.UserAccount;
import tz.business.eCard.repositories.CardGroupRepository;
import tz.business.eCard.repositories.GroupRepository;
import tz.business.eCard.repositories.UserAccountRepository;
import tz.business.eCard.services.GroupService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private LoggedUser loggedUser;
    @Autowired
    private CardGroupRepository cardGroupRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public Response<CardGroup> createUpdateGroup(GroupDto groupDto) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new Response<>(true , ResponseCode.UNAUTHORIZED , "Unauthorized access");
            }
            if(groupDto.getGroupUuid() == null || groupDto.getGroupUuid().isEmpty()) {
                if(groupDto.getGroupName() == null || groupDto.getGroupName().isEmpty()) {
                    return new Response<>(true , ResponseCode.NULL_ARGUMENT , "Group name is required");
                }
                CardGroup group = new CardGroup();
                group.setGroupName(groupDto.getGroupName());
                group.setActive(true);
                group.setOwner(user);
                return new Response<>(false , ResponseCode.SUCCESS, "Group created successfully" , groupRepository.save(group));
            }else {
                Optional<CardGroup> group = groupRepository.findFirstByUuid(groupDto.getGroupUuid());
                if(group.isPresent()) {
                    CardGroup groupToUpdate = group.get();
                    groupToUpdate.setGroupName(groupDto.getGroupName());
                    groupToUpdate.setActive(true);
                    return new Response<>(false, ResponseCode.CREATED,"Group Updated Successfully", cardGroupRepository.save(groupToUpdate) );
                }else{
                    return new Response<>(false , ResponseCode.NOT_FOUND , "Group not found");
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new Response<>(true , ResponseCode.BAD_REQUEST , "Unknown Error");
    }

    @Override
    public Response<CardGroup> deleteGroup(String groupUuid) {
        try{
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new Response<>(true , ResponseCode.UNAUTHORIZED , "Unauthorized access");
            }
            Optional<CardGroup> group = cardGroupRepository.findFirstByUuid(groupUuid);
            if(group.isPresent()) {
                CardGroup groupToDelete = group.get();
                groupToDelete.setActive(false);
                groupToDelete.setDeleted(true);
                cardGroupRepository.delete(group.get());
                return new Response<>(false , ResponseCode.SUCCESS , "Group deleted" , groupToDelete);
            }else {
                return new Response<>(true , ResponseCode.NOT_FOUND , "No group with uuid " + groupUuid +" found");
            }
        }catch (Exception e) {
            log.error("An error occurred {}" ,e.getMessage());
        }
        return new Response<>(true , ResponseCode.BAD_REQUEST , "Unknown Error");
    }

    @Override
    public Response<CardGroup> getGroup(String groupUuid) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return new Response<>(true , ResponseCode.UNAUTHORIZED , "Unauthorized access");
            }
            Optional<CardGroup> group = cardGroupRepository.findFirstByUuid(groupUuid);
            if(group.isPresent()) {
                CardGroup groupToGet = group.get();
                return new Response<>(false, ResponseCode.SUCCESS , "Group found and retrieved",groupToGet);
            }
        }catch (Exception e) {
            log.error("An error occurred{}" , e.getMessage());
        }
        return new Response<>(true , ResponseCode.BAD_REQUEST , "Unknown Error");
    }

    @Override
    public Page<CardGroup> getAllUserGroups(Pageable pageable) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            Optional<UserAccount> owner =  userAccountRepository.findFirstByUuid(user.getUuid());
            if(owner.isPresent()) {
                UserAccount userAccount = owner.get();
                Page<CardGroup> allUserGroups = cardGroupRepository.getAllByOwnerOrderByGroupNameAsc(userAccount,pageable);
                if (allUserGroups.getTotalElements() > 0) {
                    return new PageImpl<>(allUserGroups.getContent());
                }else{
                    return null;
                }
            }
        }catch (Exception e) {
            log.error("An error occurred{}" , e.getMessage());
        }
        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public List<CardGroup> getGroupCards(String groupUuid) {
        try {
            UserAccount user = loggedUser.getUserAccount();
            if(user == null) {
                return null;
            }
            Optional<CardGroup> group = cardGroupRepository.findFirstByUuid(groupUuid);
            if(group.isPresent()) {
                CardGroup groupToGet = group.get();
                return List.of(groupToGet);
            }
        }catch (Exception e){
            log.error("An error occurred{}" ,e.getMessage());
        }
        return List.of((CardGroup) null);
    }
}
