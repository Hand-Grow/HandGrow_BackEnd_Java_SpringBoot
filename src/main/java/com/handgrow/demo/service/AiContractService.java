package com.handgrow.demo.service;

import com.handgrow.demo.dto.response.DraftContractResponse;
import com.handgrow.demo.entity.BulkSale;

public interface AiContractService {
    DraftContractResponse extractContractInfo(String chatHistory, BulkSale bulkSale);
}
