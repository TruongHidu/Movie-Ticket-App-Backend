package com.example.movie_ticket_.service;

import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    // ✅ Lấy tất cả phương thức thanh toán
    public List<PaymentMethod> getAll() {
        return paymentMethodRepository.findAll();
    }

    // ✅ Lấy phương thức theo ID
    public PaymentMethod getById(Long id) {
        return paymentMethodRepository.findById(id).orElse(null);
    }

    // ✅ Thêm mới phương thức thanh toán
    public PaymentMethod add(PaymentMethod method) {
        return paymentMethodRepository.save(method);
    }

    // ✅ Lưu (dùng cho edit)
    public void save(PaymentMethod method) {
        paymentMethodRepository.save(method);
    }

    // ✅ Thay đổi trạng thái (1 ↔ 0)
    public void changeStatus(Long id, int newStatus) {
        PaymentMethod method = getById(id);
        if (method != null) {
            method.setStatus(newStatus);
            paymentMethodRepository.save(method);
        }
    }

    // ✅ Xóa phương thức
    public void delete(Long id) {
        paymentMethodRepository.deleteById(id);
    }

    // ✅ Cập nhật ảnh (khi upload lại logo mới)
    public void updateImage(Long id, String imageUrl) {
        PaymentMethod method = getById(id);
        if (method != null) {
            method.setImageUrl(imageUrl);
            paymentMethodRepository.save(method);
        }
    }
}
