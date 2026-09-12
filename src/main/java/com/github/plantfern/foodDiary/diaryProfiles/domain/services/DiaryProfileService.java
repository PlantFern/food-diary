package com.github.plantfern.foodDiary.diaryProfiles.domain.services;


import com.github.plantfern.foodDiary.diaryProfiles.api.DiaryProfileApi;
import com.github.plantfern.foodDiary.diaryProfiles.api.events.DiaryProfileCreated;
import com.github.plantfern.foodDiary.diaryProfiles.domain.DiaryProfileMapper;
import com.github.plantfern.foodDiary.diaryProfiles.domain.dto.DiaryProfileDto;
import com.github.plantfern.foodDiary.diaryProfiles.domain.entities.DiaryProfileEntity;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.DiaryProfileRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.repositories.GenderRepository;
import com.github.plantfern.foodDiary.diaryProfiles.domain.security.DiaryProfilePolicy;

import com.github.plantfern.foodDiary.users.api.CurrentUser;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class DiaryProfileService implements DiaryProfileApi {

    private final DiaryProfilePolicy diaryProfilePolicy;
    private final CurrentUser currentUser;

    private final DiaryProfileRepository diaryProfileRepository;
    private final GenderRepository genderRepository;
    private final DiaryProfileMapper mapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public DiaryProfileService(
            DiaryProfileRepository diaryProfileRepository,
            GenderRepository genderRepository,
            DiaryProfileMapper mapper,

            CurrentUser currentUser,
            DiaryProfilePolicy diaryProfilePolicy,

            ApplicationEventPublisher applicationEventPublisher
    ){
        this.diaryProfileRepository = diaryProfileRepository;
        this.genderRepository = genderRepository;
        this.mapper = mapper;

        this.currentUser = currentUser;
        this.diaryProfilePolicy = diaryProfilePolicy;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Transactional
    public void create(Float height, LocalDate birthDate, Long genderId){
        var actorUser = currentUser.requireId();

        if (diaryProfileRepository.existsByUserId(actorUser)){
            throw new IllegalStateException("Diary profile for user already exists");
        }

        applicationEventPublisher.publishEvent(
                new DiaryProfileCreated(
                        actorUser
                )
                );

        var gender = genderRepository.findById(genderId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Gender with id: " + genderId + "doesn't exist"
                )
        );

        diaryProfileRepository.save(new DiaryProfileEntity(
                actorUser,
                height,
                birthDate,
                gender
        ));
    }

    @Transactional
    public void update(
            Long id,
            Float height,
            LocalDate birthDate,
            Long genderId
    ){
        var profile = diaryProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Diary profile with id: " + id + " doesn't exist"
                        ));

        diaryProfilePolicy.ensureIsOwner(
                currentUser,
                profile.getUserId()
        );

        profile.setHeight(height);
        profile.setBirthDate(birthDate);
        profile.setGender(
                this.genderRepository
                    .findById(genderId)
                    .orElseThrow(() -> new EntityNotFoundException(
                                    "Gender with id: " + genderId + "doesn't exist"
                            )));

        diaryProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    List<Long> getOwnerUserIdList(List<Long> diaryProfileIds){

        return diaryProfileRepository.findAllUserIdsByIdIn(diaryProfileIds)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DiaryProfileEntity> findAllById(Collection<Long> ids) {

        var diaryProfiles = diaryProfileRepository
                .findAllById(ids)
                .stream()
                .toList();

        diaryProfilePolicy.ensureCanGetAll(
                currentUser,
                diaryProfiles
                        .stream()
                        .map(DiaryProfileEntity::getUserId)
                        .toList());

        return diaryProfiles;
    }

    @Transactional(readOnly = true)
    public DiaryProfileDto findById(Long diaryProfileId) {

        var targetDiaryProfile = findById(diaryProfileId);

        diaryProfilePolicy.ensureCanGet(
                currentUser,
                targetDiaryProfile.userId());

        return targetDiaryProfile;
    }


    //region Internal methods

    @Override
    @Transactional(readOnly = true)
    public DiaryProfileDto findByIdInternal(Long diaryProfileId) {

        var targetDiaryProfile = diaryProfileRepository
                .findById(diaryProfileId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Diary profile not found")
                );

        return mapper.toDto(targetDiaryProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdInternal(Long id) {
        return diaryProfileRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOwnerUserIdInternal(Long diaryProfileId){

        return diaryProfileRepository
                .findUserIdById(diaryProfileId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Dairy profile with such id not found")
                );
    }
    //endregion
}
