package com.artistack.instrument.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.ProjectInstrument;
import com.artistack.instrument.domain.UserInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.InstrumentRepository;
import com.artistack.instrument.repository.ProjectInstrumentRepository;
import com.artistack.instrument.repository.UserInstrumentRepository;
import com.artistack.user.domain.User;
import com.artistack.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class InstrumentService {

    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserInstrumentRepository userInstrumentRepository;
    private final ProjectInstrumentRepository projectInstrumentRepository;

    @PostConstruct
    public void initialize() {
        List<String> names = List.of("", "피아노", "기타", "베이스", "드럼", "보컬", "그외 악기");
        List<String> imgUrls = List.of(
            "",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/piano300*300.png",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/guitar300*300.png",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/base300*300.png",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/drum300*300.png",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/vocal300*300.png",
            "https://artistack-bucket.s3.ap-northeast-2.amazonaws.com/instrument/etc300*300.png");

        for (int i = 1; i < names.size(); i++) {
            instrumentRepository.save(
                Instrument.builder().id((long) i).name(names.get(i)).imgUrl(imgUrls.get(i)).build());
        }
    }

    public List<InstrumentDto> getAll() {
        return instrumentRepository.findAll().stream().map(InstrumentDto::response).collect(Collectors.toList());
    }

    public List<InstrumentDto> getByUserId(Long userId) {
        return userInstrumentRepository.findByUserId(userId).stream().map(UserInstrument::getInstrument)
            .map(InstrumentDto::response).collect(Collectors.toList());
    }

    public List<InstrumentDto> insertByUserId(List<InstrumentDto> instrumentDtos, Long userId) {
        User user = userRepository.findById(userId).get();

        if (!isEmpty(instrumentDtos)) {
            for (InstrumentDto instrumentDto : instrumentDtos) {
                Instrument instrument = instrumentRepository.findById(instrumentDto.getId())
                    .orElseThrow(
                        () -> new GeneralException(Code.INSTRUMENT_ID_NOT_VALID, instrumentDto.getId().toString()));
                userInstrumentRepository.save(UserInstrument.builder().user(user).instrument(instrument).build());
            }
        }

        return getByUserId(userId);
    }

    public List<InstrumentDto> getByProjectId(Long projectId) {
        return projectInstrumentRepository.findByProjectId(projectId).stream().map(ProjectInstrument::getInstrument)
                .map(InstrumentDto::response).collect(Collectors.toList());
    }
}