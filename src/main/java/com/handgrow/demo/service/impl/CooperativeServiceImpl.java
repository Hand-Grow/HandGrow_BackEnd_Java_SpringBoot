package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CooperativeSearchDto;
import com.handgrow.demo.dto.response.CooperativeResponse;
import com.handgrow.demo.entity.Cooperative;
import com.handgrow.demo.mapper.CooperativeMapper;
import com.handgrow.demo.repository.CooperativeRepository;
import com.handgrow.demo.service.CooperativeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CooperativeServiceImpl implements CooperativeService {

    private final CooperativeRepository cooperativeRepository;
    private final CooperativeMapper cooperativeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CooperativeResponse> searchCooperatives(CooperativeSearchDto searchDto) {
        List<Cooperative> cooperatives;

        String commune = searchDto.getCommune();
        String province = searchDto.getProvince();
        var produce = searchDto.getProduce();

        // Apply filters based on provided criteria
        if (commune != null && province != null && produce != null) {
            cooperatives = cooperativeRepository.findByCommuneAndProvinceAndProduce(commune, province, produce);
        } else if (commune != null && province != null) {
            cooperatives = cooperativeRepository.findByCommuneAndProvince(commune, province);
        } else if (commune != null && produce != null) {
            cooperatives = cooperativeRepository.findByCommuneAndProduce(commune, produce);
        } else if (province != null && produce != null) {
            cooperatives = cooperativeRepository.findByProvinceAndProduce(province, produce);
        } else if (commune != null) {
            cooperatives = cooperativeRepository.findByCommune(commune);
        } else if (province != null) {
            cooperatives = cooperativeRepository.findByProvince(province);
        } else if (produce != null) {
            cooperatives = cooperativeRepository.findByProduce(produce);
        } else {
            cooperatives = cooperativeRepository.findAllOrderByMemberCount();
        }

        return cooperatives.stream().map(cooperativeMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CooperativeResponse> getAllCooperatives() {
        List<Cooperative> cooperatives = cooperativeRepository.findAllOrderByMemberCount();
        return cooperatives.stream().map(cooperativeMapper::toResponse).collect(Collectors.toList());
    }
}
