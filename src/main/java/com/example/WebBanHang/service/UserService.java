package com.example.WebBanHang.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WebBanHang.dto.ApiResponse;
import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    

    
    public ResponseEntity<ApiResponse<Object>> login(String identifier, String password, HttpSession session) {
        try {
            User user = userRepository.findByEmail(identifier);
            if (user == null) {
                user = userRepository.findByUsername(identifier);
            }

            if (user == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tài khoản không tồn tại", null));
            }

            if (Boolean.FALSE.equals(user.getIsActive())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Tài khoản đã bị vô hiệu hóa", null));
            }

            boolean passwordOk = false;
            boolean needsUpdate = false;

            try {
                // Thử kiểm tra bằng BCrypt
                passwordOk = BCrypt.checkpw(password, user.getPassword());
            } catch (Exception e) {
                // Nếu lỗi (do mật khẩu trong DB chưa hash), kiểm tra trực tiếp
                if (user.getPassword().equals(password)) {
                    passwordOk = true;
                    needsUpdate = true; // Đánh dấu cần cập nhật hash
                }
            }

            if (!passwordOk) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Sai mật khẩu", null));
            }

            // Nếu mật khẩu đúng nhưng chưa hash, thực hiện hash và lưu lại
            if (needsUpdate) {
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                userRepository.save(user);
                System.out.println("✅ [UserService] Đã tự động Hash lại mật khẩu cho: " + identifier);
            }

            session.setAttribute("currentUser", user);

            String message = "ADMIN".equals(user.getRole()) ? "Đăng nhập Admin thành công"
                           : "STAFF".equals(user.getRole()) ? "Đăng nhập Staff thành công"
                           : "Đăng nhập thành công";

            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", message, user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Lỗi Server", e.getMessage()));
        }
    }

    // ===================== REGISTER (CLIENT only) =====================

    public ResponseEntity<ApiResponse<Object>> register(User user) {
        try {
            User existing = userRepository.findByEmail(user.getEmail());
            if (existing != null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>("ERROR", "Email đã tồn tại", null)
                );
            }
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            user.setRole("CLIENT");
            if (user.getIsActive() == null) {
                user.setIsActive(true);
            }
            userRepository.save(user);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Đăng ký thành công", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Đăng ký thất bại", e.getMessage())
            );
        }
    }

    /** Tạo tài khoản STAFF - chỉ gọi từ AdminController */
    public ResponseEntity<ApiResponse<Object>> registerStaff(User user) {
        try {
            if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>("ERROR", "Email đã tồn tại", null)
                );
            }
            if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()) != null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>("ERROR", "Username đã tồn tại", null)
                );
            }
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
            user.setRole("STAFF"); // Đảm bảo không thể thay đổi role
            if (user.getIsActive() == null) user.setIsActive(true);
            userRepository.save(user);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Tạo tài khoản Staff thành công", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Tạo tài khoản Staff thất bại", e.getMessage())
            );
        }
    }


    // ===================== SESSION HELPERS =====================

    public ResponseEntity<ApiResponse<Object>> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Đăng xuất thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Đăng xuất thất bại", e.getMessage())
            );
        }
    }

    public ResponseEntity<ApiResponse<Object>> getProfile(HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                return ResponseEntity.ok().body(
                    new ApiResponse<>("SUCCESS", "Lấy thông tin thành công", user)
                );
            }
            return ResponseEntity.status(401).body(
                new ApiResponse<>("ERROR", "Chưa đăng nhập", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Lỗi khi lấy thông tin", e.getMessage())
            );
        }
    }

    public ResponseEntity<ApiResponse<Object>> updateProfile(User updatedData, HttpSession session) {
        try {
            User sessionUser = (User) session.getAttribute("currentUser");
            if (sessionUser == null) {
                return ResponseEntity.status(401).body(
                    new ApiResponse<>("ERROR", "Chưa đăng nhập", null)
                );
            }
            User user = userRepository.findById(sessionUser.getId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>("ERROR", "Không tìm thấy người dùng", null)
                );
            }
            user.setFullName(updatedData.getFullName());
            user.setPhone(updatedData.getPhone());
            user.setDateOfBirth(updatedData.getDateOfBirth());
            user.setGender(updatedData.getGender());
            userRepository.save(user);
            session.setAttribute("currentUser", user);
            return ResponseEntity.ok().body(
                new ApiResponse<>("SUCCESS", "Cập nhật thông tin thành công", user)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>("ERROR", "Cập nhật thất bại", e.getMessage())
            );
        }
    }

    public java.util.List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public ResponseEntity<ApiResponse<Object>> changePassword(String oldPwd, String newPwd, HttpSession session) {
        try {
            User sessionUser = (User) session.getAttribute("currentUser");
            if (sessionUser == null) {
                return ResponseEntity.status(401).body(new ApiResponse<>("ERROR", "Chưa đăng nhập", null));
            }
            User user = userRepository.findById(sessionUser.getId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Người dùng không tồn tại", null));
            }

            if (!BCrypt.checkpw(oldPwd, user.getPassword())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "Mật khẩu hiện tại không đúng", null));
            }

            user.setPassword(BCrypt.hashpw(newPwd, BCrypt.gensalt()));
            userRepository.save(user);
            return ResponseEntity.ok().body(new ApiResponse<>("SUCCESS", "Đổi mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>("ERROR", "Lỗi server", e.getMessage()));
        }
    }
}
