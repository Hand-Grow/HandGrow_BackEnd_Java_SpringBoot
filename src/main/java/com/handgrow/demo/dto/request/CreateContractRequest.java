package com.handgrow.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateContractRequest {

    /** roomId từ MongoDB — dùng để lookup BulkSale + các bên liên quan */
    @NotBlank(message = "roomId không được để trống")
    private String roomId;

    /** Số lượng đã thỏa thuận (đơn vị: tấn) */
    @NotNull(message = "agreedQuantity không được để trống")
    private BigDecimal agreedQuantity;

    /** Đơn giá đã thỏa thuận (VNĐ/kg) */
    @NotNull(message = "agreedPrice không được để trống")
    private BigDecimal agreedPrice;

    /** Ngày giao hàng */
    @NotNull(message = "deliveryDate không được để trống")
    private LocalDate deliveryDate;

    /** Điều khoản bổ sung (tùy chọn) */
    private String terms;

    /** Địa điểm giao hàng (tùy chọn) */
    private String deliveryLocation;
}
