package com.github.plantfern.foodDiary.specialists.domain;


import com.github.plantfern.foodDiary.specialists.domain.repositories.UserRelationRepository;
import com.github.plantfern.foodDiary.users.api.CurrentUser;
import com.github.plantfern.foodDiary.users.api.UserApi;
import org.springframework.stereotype.Service;


@Service
public class UserRelationService {

    private final CurrentUser currentUser;
    private final UserApi userApi;

    private final UserRelationMapper userRelationMapper;
    private final UserRelationRepository userRelationRepository;

    public UserRelationService(
            CurrentUser currentUser,
            UserApi userApi,

            UserRelationMapper userRelationMapper,
            UserRelationRepository userRelationRepository
    ){
        this.currentUser = currentUser;
        this.userApi = userApi;

        this.userRelationMapper = userRelationMapper;
        this.userRelationRepository = userRelationRepository;
    }
}
