package com.artistack.instrument.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import com.artistack.instrument.domain.Instrument;
import com.artistack.instrument.domain.UserInstrument;
import com.artistack.instrument.dto.InstrumentDto;
import com.artistack.instrument.repository.InstrumentRepository;
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

    @PostConstruct
    public void initialize() {
        List<String> names = List.of("보컬", "피아노", "드럼", "기타", "베이스", "신디사이저", "바이올린", "기타");
        List<String> imgUrls = List.of("https://...", "https://...", "https://...", "https://...", "https://...",
            "https://...", "https://...", "https://...");

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
}