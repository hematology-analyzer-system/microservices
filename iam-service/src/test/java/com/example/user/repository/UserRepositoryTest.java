//package com.example.user.repository;
//
//import com.example.user.config.AuditTestConfig;
//import com.example.user.exception.ResourceNotFoundException;
//import com.example.user.model.User;
////import org.junit.jupiter.api.Assertions;
//import com.example.user.model.UserAuditInfo;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.*;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
////import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//@Import(AuditTestConfig.class)
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User createTestUser(String name, String email, String phone, String identifyNum) {
//        User newUser = new User();
//        newUser.setFullName(name);
//        newUser.setEmail(email);
//        newUser.setPhone(phone);
//        newUser.setIdentifyNum(identifyNum);
//        newUser.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        newUser.setGender("Nam");
//        newUser.setPassword("123123123");
//        newUser.setDate_of_Birth("1/1/1992");
//        newUser.setCreate_at(LocalDateTime.now());
//        newUser.setUpdate_at(LocalDateTime.now());
//
//        return newUser;
//    }
//
//    @Test
//    public void UserRepository_Save_ReturnSavedUser() {
//        //Arrange
//        User newUser = new User();
//        newUser.setFullName("Nguyễn Văn A");
//        newUser.setEmail("nguyenvana@gmail.com");
//        newUser.setPhone("0123456789");
//        newUser.setIdentifyNum("0123456789");
//        newUser.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        newUser.setGender("Nam");
//        newUser.setPassword("123123123");
////        newUser.setAge(32);
//        newUser.setDate_of_Birth("1/1/1992");
//        newUser.setCreate_at(LocalDateTime.now());
//        newUser.setUpdate_at(LocalDateTime.now());
//
//        //Act
//        User savedUser = userRepository.save(newUser);
//
//
//        //Assert
//        Assertions.assertThat(savedUser).isNotNull();
//        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
//    }
//
//    @Test
//    public void UserRepository_GetAll_ReturnAllUsers() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        User User2 = new User();
//        User1.setFullName("Nguyễn Văn B");
//        User1.setEmail("nguyenvanb@gmail.com");
//        User1.setPhone("00000001");
//        User1.setIdentifyNum("00000001");
//        User1.setAddress("12B Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nữ");
//        User1.setPassword("123123123");
//        //User1.setAge(33);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//        userRepository.save(User2);
//
//        //Act
//        List<User> allUsers = userRepository.findAll();
//
//        //Assert
//        Assertions.assertThat(allUsers).isNotNull();
//        Assertions.assertThat(allUsers.size()).isEqualTo(2);
//    }
//
//    @Test
//    public void UserRepository_GetAll_ReturnEmpty() {
//        //Arrange
//
//        //Act
//        List<User> allUsers = userRepository.findAll();
//
//        //Assert
//        Assertions.assertThat(allUsers).isEmpty();
//        Assertions.assertThat(allUsers.size()).isEqualTo(0);
//    }
//
//    @Test
//    public void UserRepository_FindById_ReturnUser() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        User resultUser = userRepository.findById(User1.getId()).orElseThrow(()-> new ResourceNotFoundException("User","Id",User1.getId()));
//
//        //Assert
//        Assertions.assertThat(resultUser).isNotNull();
//    }
//
//    @Test
//    public void UserRepository_FindById_ReturnEmpty() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        Optional<User> resultUser = userRepository.findById(User1.getId()+1);
//
//        //Assert
//        Assertions.assertThat(resultUser).isEmpty();
//    }
//
//    @Test
//    public void UserRepository_FindByPhone_ReturnUser() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        User resultUser = userRepository.findByPhone(User1.getPhone()).orElseThrow(()-> new ResourceNotFoundException("User","Phone",User1.getPhone()));
//
//        //Assert
//        Assertions.assertThat(resultUser).isNotNull();
//    }
//
//    @Test
//    public void UserRepository_FindByPhone_ReturnEmpty() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        Optional<User> resultUser = userRepository.findByPhone("00000001");
//
//        //Assert
//        Assertions.assertThat(resultUser).isEmpty();
//    }
//
//    @Test
//    public void UserRepository_UpdateUser_ReturnUser() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        User resultUser = userRepository.findById(User1.getId()).orElseThrow(()-> new ResourceNotFoundException("User","Id",User1.getId()));
//        //resultUser.setAge(resultUser.getAge()+1);
//        resultUser.setFullName("Nguyễn Văn C");
//
//        User UpdateUser = userRepository.save(resultUser);
//
//        //Assert
//        Assertions.assertThat(UpdateUser).isNotNull();
//        Assertions.assertThat(UpdateUser.getId()).isEqualTo(User1.getId());
//        Assertions.assertThat(UpdateUser.getFullName()).isEqualTo("Nguyễn Văn C");
//        //Assertions.assertThat(UpdateUser.getAge()).isEqualTo(33);
//    }
//
//    @Test
//    public void UserRepository_DeleteUser_ReturnEmpty() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        userRepository.deleteById(User1.getId());
//        Optional<User> resultUser = userRepository.findById(User1.getId());
//
//        //Assert
//        Assertions.assertThat(resultUser).isEmpty();
//    }
//
//    @Test
//    public void UserRepository_DeleteUserNotFound_ReturnDbUnchange() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        userRepository.deleteById(User1.getId()+1);
//        Optional<User> resultUser = userRepository.findById(User1.getId());
//
//        //Assert
//        Assertions.assertThat(resultUser).isNotNull();
//    }
//
//    @Test
//    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSingleMatchUser() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        User User2 = new User();
//        User2.setFullName("Nguyễn Văn B");
//        User2.setEmail("nguyenvanb@gmail.com");
//        User2.setPhone("00000001");
//        User2.setIdentifyNum("00000001");
//        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
//        User2.setGender("Nam");
//        User2.setPassword("123123123");
//        //User2.setAge(33);
//        User2.setDate_of_Birth("2/2/1991");
//        User2.setCreate_at(LocalDateTime.now());
//        User2.setUpdate_at(LocalDateTime.now());
//
//        User User3 = new User();
//        User3.setFullName("Trần Thị C");
//        User3.setEmail("tranthic@gmail.com");
//        User3.setPhone("00000002");
//        User3.setIdentifyNum("00000002");
//        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
//        User3.setGender("Nữ");
//        User3.setPassword("123123123");
//        //User3.setAge(28);
//        User3.setDate_of_Birth("3/3/1996");
//        User3.setCreate_at(LocalDateTime.now());
//        User3.setUpdate_at(LocalDateTime.now());
//
//        User User4 = new User();
//        User4.setFullName("Lê Văn D");
//        User4.setEmail("levand@gmail.com");
//        User4.setPhone("00000003");
//        User4.setIdentifyNum("00000003");
//        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
//        User4.setGender("Nam");
//        User4.setPassword("123123123");
//        //User4.setAge(40);
//        User4.setDate_of_Birth("4/4/1984");
//        User4.setCreate_at(LocalDateTime.now());
//        User4.setUpdate_at(LocalDateTime.now());
//
//        User User5 = new User();
//        User5.setFullName("Phạm Thị E");
//        User5.setEmail("phamthie@gmail.com");
//        User5.setPhone("00000004");
//        User5.setIdentifyNum("00000004");
//        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
//        User5.setGender("Nữ");
//        User5.setPassword("123123123");
//        //User5.setAge(25);
//        User5.setDate_of_Birth("5/5/1999");
//        User5.setCreate_at(LocalDateTime.now());
//        User5.setUpdate_at(LocalDateTime.now());
//
//        User User6 = new User();
//        User6.setFullName("Đỗ Văn F");
//        User6.setEmail("dovanf@gmail.com");
//        User6.setPhone("00000005");
//        User6.setIdentifyNum("00000005");
//        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
//        User6.setGender("Nam");
//        User6.setPassword("123123123");
//        //User6.setAge(36);
//        User6.setDate_of_Birth("6/6/1988");
//        User6.setCreate_at(LocalDateTime.now());
//        User6.setUpdate_at(LocalDateTime.now());
//
//        User User7 = new User();
//        User7.setFullName("Ngô Thị G");
//        User7.setEmail("ngothig@gmail.com");
//        User7.setPhone("00000006");
//        User7.setIdentifyNum("00000006");
//        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
//        User7.setGender("Nữ");
//        User7.setPassword("123123123");
//        //User7.setAge(30);
//        User7.setDate_of_Birth("7/7/1994");
//        User7.setCreate_at(LocalDateTime.now());
//        User7.setUpdate_at(LocalDateTime.now());
//
//        User User8 = new User();
//        User8.setFullName("Vũ Văn H");
//        User8.setEmail("vuvanh@gmail.com");
//        User8.setPhone("00000007");
//        User8.setIdentifyNum("00000007");
//        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
//        User8.setGender("Nam");
//        User8.setPassword("123123123");
//        //User8.setAge(27);
//        User8.setDate_of_Birth("8/8/1997");
//        User8.setCreate_at(LocalDateTime.now());
//        User8.setUpdate_at(LocalDateTime.now());
//
//        User User9 = new User();
//        User9.setFullName("Bùi Thị I");
//        User9.setEmail("buithii@gmail.com");
//        User9.setPhone("00000008");
//        User9.setIdentifyNum("00000008");
//        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
//        User9.setGender("Nữ");
//        User9.setPassword("123123123");
//        //User9.setAge(29);
//        User9.setDate_of_Birth("9/9/1995");
//        User9.setCreate_at(LocalDateTime.now());
//        User9.setUpdate_at(LocalDateTime.now());
//
//        User User10 = new User();
//        User10.setFullName("Hoàng Văn K");
//        User10.setEmail("hoangvank@gmail.com");
//        User10.setPhone("00000009");
//        User10.setIdentifyNum("00000009");
//        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
//        User10.setGender("Nam");
//        User10.setPassword("123123123");
//        //User10.setAge(35);
//        User10.setDate_of_Birth("10/10/1989");
//        User10.setCreate_at(LocalDateTime.now());
//        User10.setUpdate_at(LocalDateTime.now());
//
//
//        userRepository.save(User1);
//        userRepository.save(User2);
//        userRepository.save(User3);
//        userRepository.save(User4);
//        userRepository.save(User5);
//        userRepository.save(User6);
//        userRepository.save(User7);
//        userRepository.save(User8);
//        userRepository.save(User9);
//        userRepository.save(User10);
//
//
//        //Act
//        List<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("Nguyễn Văn A");
//
//        //Assert
//        Assertions.assertThat(allUsers).isNotNull();
//        Assertions.assertThat(allUsers.size()).isEqualTo(1);
//        Assertions.assertThat(allUsers.get(0)).isEqualTo(User1);
//    }
//
//    @Test
//    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnMultipleMatchUsers() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        User User2 = new User();
//        User2.setFullName("Nguyễn Văn B");
//        User2.setEmail("nguyenvanb@gmail.com");
//        User2.setPhone("00000001");
//        User2.setIdentifyNum("00000001");
//        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
//        User2.setGender("Nam");
//        User2.setPassword("123123123");
//        //User2.setAge(33);
//        User2.setDate_of_Birth("2/2/1991");
//        User2.setCreate_at(LocalDateTime.now());
//        User2.setUpdate_at(LocalDateTime.now());
//
//        User User3 = new User();
//        User3.setFullName("Trần Thị C");
//        User3.setEmail("tranthic@gmail.com");
//        User3.setPhone("00000002");
//        User3.setIdentifyNum("00000002");
//        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
//        User3.setGender("Nữ");
//        User3.setPassword("123123123");
//        //User3.setAge(28);
//        User3.setDate_of_Birth("3/3/1996");
//        User3.setCreate_at(LocalDateTime.now());
//        User3.setUpdate_at(LocalDateTime.now());
//
//        User User4 = new User();
//        User4.setFullName("Lê Văn D");
//        User4.setEmail("levand@gmail.com");
//        User4.setPhone("00000003");
//        User4.setIdentifyNum("00000003");
//        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
//        User4.setGender("Nam");
//        User4.setPassword("123123123");
//        //User4.setAge(40);
//        User4.setDate_of_Birth("4/4/1984");
//        User4.setCreate_at(LocalDateTime.now());
//        User4.setUpdate_at(LocalDateTime.now());
//
//        User User5 = new User();
//        User5.setFullName("Phạm Thị E");
//        User5.setEmail("phamthie@gmail.com");
//        User5.setPhone("00000004");
//        User5.setIdentifyNum("00000004");
//        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
//        User5.setGender("Nữ");
//        User5.setPassword("123123123");
//        //User5.setAge(25);
//        User5.setDate_of_Birth("5/5/1999");
//        User5.setCreate_at(LocalDateTime.now());
//        User5.setUpdate_at(LocalDateTime.now());
//
//        User User6 = new User();
//        User6.setFullName("Đỗ Văn F");
//        User6.setEmail("dovanf@gmail.com");
//        User6.setPhone("00000005");
//        User6.setIdentifyNum("00000005");
//        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
//        User6.setGender("Nam");
//        User6.setPassword("123123123");
//        //User6.setAge(36);
//        User6.setDate_of_Birth("6/6/1988");
//        User6.setCreate_at(LocalDateTime.now());
//        User6.setUpdate_at(LocalDateTime.now());
//
//        User User7 = new User();
//        User7.setFullName("Ngô Thị G");
//        User7.setEmail("ngothig@gmail.com");
//        User7.setPhone("00000006");
//        User7.setIdentifyNum("00000006");
//        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
//        User7.setGender("Nữ");
//        User7.setPassword("123123123");
//        //User7.setAge(30);
//        User7.setDate_of_Birth("7/7/1994");
//        User7.setCreate_at(LocalDateTime.now());
//        User7.setUpdate_at(LocalDateTime.now());
//
//        User User8 = new User();
//        User8.setFullName("Vũ Văn H");
//        User8.setEmail("vuvanh@gmail.com");
//        User8.setPhone("00000007");
//        User8.setIdentifyNum("00000007");
//        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
//        User8.setGender("Nam");
//        User8.setPassword("123123123");
//        //User8.setAge(27);
//        User8.setDate_of_Birth("8/8/1997");
//        User8.setCreate_at(LocalDateTime.now());
//        User8.setUpdate_at(LocalDateTime.now());
//
//        User User9 = new User();
//        User9.setFullName("Bùi Thị I");
//        User9.setEmail("buithii@gmail.com");
//        User9.setPhone("00000008");
//        User9.setIdentifyNum("00000008");
//        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
//        User9.setGender("Nữ");
//        User9.setPassword("123123123");
//        //User9.setAge(29);
//        User9.setDate_of_Birth("9/9/1995");
//        User9.setCreate_at(LocalDateTime.now());
//        User9.setUpdate_at(LocalDateTime.now());
//
//        User User10 = new User();
//        User10.setFullName("Hoàng Văn K");
//        User10.setEmail("hoangvank@gmail.com");
//        User10.setPhone("00000009");
//        User10.setIdentifyNum("00000009");
//        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
//        User10.setGender("Nam");
//        User10.setPassword("123123123");
//        //User10.setAge(35);
//        User10.setDate_of_Birth("10/10/1989");
//        User10.setCreate_at(LocalDateTime.now());
//        User10.setUpdate_at(LocalDateTime.now());
//
//
//        userRepository.save(User1);
//        userRepository.save(User2);
//        userRepository.save(User3);
//        userRepository.save(User4);
//        userRepository.save(User5);
//        userRepository.save(User6);
//        userRepository.save(User7);
//        userRepository.save(User8);
//        userRepository.save(User9);
//        userRepository.save(User10);
//
//
//        //Act
//        Sort sort = Sort.by("fullName").ascending();
//        Pageable pageable = PageRequest.of(0, 5, sort);
//        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("nguyễn", pageable);
//
//        //Assert
//        Assertions.assertThat(allUsers).isNotNull();
//        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(2);
//        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(User1);
//        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(User2);
//    }
//
//    @Test
//    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSelectedNumberOfMatchUsers() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        User User2 = new User();
//        User2.setFullName("Nguyễn Văn B");
//        User2.setEmail("nguyenvanb@gmail.com");
//        User2.setPhone("00000001");
//        User2.setIdentifyNum("00000001");
//        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
//        User2.setGender("Nam");
//        User2.setPassword("123123123");
//        //User2.setAge(33);
//        User2.setDate_of_Birth("2/2/1991");
//        User2.setCreate_at(LocalDateTime.now());
//        User2.setUpdate_at(LocalDateTime.now());
//
//        User User3 = new User();
//        User3.setFullName("Trần Thị C");
//        User3.setEmail("tranthic@gmail.com");
//        User3.setPhone("00000002");
//        User3.setIdentifyNum("00000002");
//        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
//        User3.setGender("Nữ");
//        User3.setPassword("123123123");
//        //User3.setAge(28);
//        User3.setDate_of_Birth("3/3/1996");
//        User3.setCreate_at(LocalDateTime.now());
//        User3.setUpdate_at(LocalDateTime.now());
//
//        User User4 = new User();
//        User4.setFullName("Lê Văn D");
//        User4.setEmail("levand@gmail.com");
//        User4.setPhone("00000003");
//        User4.setIdentifyNum("00000003");
//        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
//        User4.setGender("Nam");
//        User4.setPassword("123123123");
////        User4.setAge(40);
//        User4.setDate_of_Birth("4/4/1984");
//        User4.setCreate_at(LocalDateTime.now());
//        User4.setUpdate_at(LocalDateTime.now());
//
//        User User5 = new User();
//        User5.setFullName("Phạm Thị E");
//        User5.setEmail("phamthie@gmail.com");
//        User5.setPhone("00000004");
//        User5.setIdentifyNum("00000004");
//        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
//        User5.setGender("Nữ");
//        User5.setPassword("123123123");
////        User5.setAge(25);
//        User5.setDate_of_Birth("5/5/1999");
//        User5.setCreate_at(LocalDateTime.now());
//        User5.setUpdate_at(LocalDateTime.now());
//
//        User User6 = new User();
//        User6.setFullName("Đỗ Văn F");
//        User6.setEmail("dovanf@gmail.com");
//        User6.setPhone("00000005");
//        User6.setIdentifyNum("00000005");
//        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
//        User6.setGender("Nam");
//        User6.setPassword("123123123");
////        User6.setAge(36);
//        User6.setDate_of_Birth("6/6/1988");
//        User6.setCreate_at(LocalDateTime.now());
//        User6.setUpdate_at(LocalDateTime.now());
//
//        User User7 = new User();
//        User7.setFullName("Ngô Thị G");
//        User7.setEmail("ngothig@gmail.com");
//        User7.setPhone("00000006");
//        User7.setIdentifyNum("00000006");
//        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
//        User7.setGender("Nữ");
//        User7.setPassword("123123123");
////        User7.setAge(30);
//        User7.setDate_of_Birth("7/7/1994");
//        User7.setCreate_at(LocalDateTime.now());
//        User7.setUpdate_at(LocalDateTime.now());
//
//        User User8 = new User();
//        User8.setFullName("Vũ Văn H");
//        User8.setEmail("vuvanh@gmail.com");
//        User8.setPhone("00000007");
//        User8.setIdentifyNum("00000007");
//        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
//        User8.setGender("Nam");
//        User8.setPassword("123123123");
////        User8.setAge(27);
//        User8.setDate_of_Birth("8/8/1997");
//        User8.setCreate_at(LocalDateTime.now());
//        User8.setUpdate_at(LocalDateTime.now());
//
//        User User9 = new User();
//        User9.setFullName("Bùi Thị I");
//        User9.setEmail("buithii@gmail.com");
//        User9.setPhone("00000008");
//        User9.setIdentifyNum("00000008");
//        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
//        User9.setGender("Nữ");
//        User9.setPassword("123123123");
////        User9.setAge(29);
//        User9.setDate_of_Birth("9/9/1995");
//        User9.setCreate_at(LocalDateTime.now());
//        User9.setUpdate_at(LocalDateTime.now());
//
//        User User10 = new User();
//        User10.setFullName("Hoàng Văn K");
//        User10.setEmail("hoangvank@gmail.com");
//        User10.setPhone("00000009");
//        User10.setIdentifyNum("00000009");
//        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
//        User10.setGender("Nam");
//        User10.setPassword("123123123");
////        User10.setAge(35);
//        User10.setDate_of_Birth("10/10/1989");
//        User10.setCreate_at(LocalDateTime.now());
//        User10.setUpdate_at(LocalDateTime.now());
//
//
//        userRepository.save(User1);
//        userRepository.save(User2);
//        userRepository.save(User3);
//        userRepository.save(User4);
//        userRepository.save(User5);
//        userRepository.save(User6);
//        userRepository.save(User7);
//        userRepository.save(User8);
//        userRepository.save(User9);
//        userRepository.save(User10);
//
//
//        //Act
//        Sort sort = Sort.by("Id").ascending();
//        Pageable pageable = PageRequest.of(0, 5, sort);
//        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("Văn", pageable);
//
//        //Assert
//
//        List<User> userList = allUsers.getContent();
//
//        for (User user : userList) {
//            System.out.println(user);
//        }
//
//        Assertions.assertThat(allUsers).isNotNull();
//        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(5);
//        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(User1);
//        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(User2);
//        Assertions.assertThat(allUsers.getContent().get(2)).isEqualTo(User4);
//        Assertions.assertThat(allUsers.getContent().get(3)).isEqualTo(User6);
//        Assertions.assertThat(allUsers.getContent().get(4)).isEqualTo(User8);
//    }
//
//    @Test
//    public void UserRepository_ExistByEmail_ReturnTrue() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        Boolean result = userRepository.existsByEmail("nguyenvana@gmail.com");
//
//        //Assert
//        Assertions.assertThat(result).isTrue();
//    }
//
//    @Test
//    public void UserRepository_ExistByEmail_ReturnTFalse() {
//        //Arrange
//        User User1 = new User();
//        User1.setFullName("Nguyễn Văn A");
//        User1.setEmail("nguyenvana@gmail.com");
//        User1.setPhone("00000000");
//        User1.setIdentifyNum("00000000");
//        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
//        User1.setGender("Nam");
//        User1.setPassword("123123123");
//        //User1.setAge(32);
//        User1.setDate_of_Birth("1/1/1992");
//        User1.setCreate_at(LocalDateTime.now());
//        User1.setUpdate_at(LocalDateTime.now());
//
//        userRepository.save(User1);
//
//        //Act
//        Boolean result = userRepository.existsByEmail("nguyenvanb@gmail.com");
//
//        //Assert
//        Assertions.assertThat(result).isFalse();
//    }
//
//}



package com.example.user.repository;

import com.example.user.config.AuditTestConfig;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(AuditTestConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User createTestUser(String name, String email, String phone, String identifyNum) {
        User newUser = new User();
        newUser.setFullName(name);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setIdentifyNum(identifyNum);
        newUser.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        newUser.setGender("Nam");
        newUser.setPassword("123123123");
        newUser.setDate_of_Birth("1/1/1992");
        newUser.setCreate_at(LocalDateTime.now());
        newUser.setUpdate_at(LocalDateTime.now());
        return newUser;
    }

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        // Arrange
        User newUser = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "0123456789", "0123456789");

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_GetAll_ReturnAllUsers() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        User user2 = createTestUser("Nguyễn Văn B", "nguyenvanb@gmail.com", "00000001", "00000001");

        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        List<User> allUsers = userRepository.findAll();

        // Assert
        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_GetAll_ReturnEmpty() {
        // Arrange - No users added

        // Act
        List<User> allUsers = userRepository.findAll();

        // Assert
        Assertions.assertThat(allUsers).isEmpty();
        Assertions.assertThat(allUsers.size()).isEqualTo(0);
    }

    @Test
    public void UserRepository_FindById_ReturnUser() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        User resultUser = userRepository.findById(user1.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", user1.getId()));

        // Assert
        Assertions.assertThat(resultUser).isNotNull();
        Assertions.assertThat(resultUser.getId()).isEqualTo(user1.getId());
    }

    @Test
    public void UserRepository_FindById_ReturnEmpty() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        Optional<User> resultUser = userRepository.findById(user1.getId() + 1); // Search for a non-existent ID

        // Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_FindByPhone_ReturnUser() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        User resultUser = userRepository.findByPhone(user1.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Phone", user1.getPhone()));

        // Assert
        Assertions.assertThat(resultUser).isNotNull();
        Assertions.assertThat(resultUser.getPhone()).isEqualTo(user1.getPhone());
    }

    @Test
    public void UserRepository_FindByPhone_ReturnEmpty() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        Optional<User> resultUser = userRepository.findByPhone("00000001"); // Search for a non-existent phone

        // Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_UpdateUser_ReturnUser() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        User foundUser = userRepository.findById(user1.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", user1.getId()));
        foundUser.setFullName("Nguyễn Văn C"); // Update the full name

        User updatedUser = userRepository.save(foundUser);

        // Assert
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getId()).isEqualTo(user1.getId());
        Assertions.assertThat(updatedUser.getFullName()).isEqualTo("Nguyễn Văn C");
    }

    @Test
    public void UserRepository_DeleteUser_ReturnEmpty() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        userRepository.deleteById(user1.getId());
        Optional<User> resultUser = userRepository.findById(user1.getId());

        // Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_DeleteUserNotFound_ReturnDbUnchange() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);
        long initialCount = userRepository.count();

        // Act
        userRepository.deleteById(user1.getId() + 1); // Attempt to delete a non-existent user
        long finalCount = userRepository.count();
        Optional<User> resultUser = userRepository.findById(user1.getId());


        // Assert
        Assertions.assertThat(resultUser).isNotNull(); // The original user should still exist
        Assertions.assertThat(initialCount).isEqualTo(finalCount); // Database count should not change
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSingleMatchUser() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        User user2 = createTestUser("Nguyễn Văn B", "nguyenvanb@gmail.com", "00000001", "00000001");
        User user3 = createTestUser("Trần Thị C", "tranthic@gmail.com", "00000002", "00000002");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Act
        List<User> foundUsers = userRepository.findByFullNameContainingIgnoreCase("Nguyễn Văn A");

        // Assert
        Assertions.assertThat(foundUsers).isNotNull();
        Assertions.assertThat(foundUsers.size()).isEqualTo(1);
        Assertions.assertThat(foundUsers.get(0)).isEqualTo(user1);
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnMultipleMatchUsers() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        User user2 = createTestUser("Nguyễn Văn B", "nguyenvanb@gmail.com", "00000001", "00000001");
        User user3 = createTestUser("Trần Thị C", "tranthic@gmail.com", "00000002", "00000002");
        User user4 = createTestUser("Lê Văn D", "levand@gmail.com", "00000003", "00000003");
        User user5 = createTestUser("Phạm Thị E", "phamthie@gmail.com", "00000004", "00000004");
        User user6 = createTestUser("Đỗ Văn F", "dovanf@gmail.com", "00000005", "00000005");
        User user7 = createTestUser("Ngô Thị G", "ngothig@gmail.com", "00000006", "00000006");
        User user8 = createTestUser("Vũ Văn H", "vuvanh@gmail.com", "00000007", "00000007");
        User user9 = createTestUser("Bùi Thị I", "buithii@gmail.com", "00000008", "00000008");
        User user10 = createTestUser("Hoàng Văn K", "hoangvank@gmail.com", "00000009", "00000009");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(user10);


        // Act
        Sort sort = Sort.by("fullName").ascending();
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("nguyễn", pageable);

        // Assert
        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(user1);
        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(user2);
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSelectedNumberOfMatchUsers() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        User user2 = createTestUser("Nguyễn Văn B", "nguyenvanb@gmail.com", "00000001", "00000001");
        User user3 = createTestUser("Trần Thị C", "tranthic@gmail.com", "00000002", "00000002");
        User user4 = createTestUser("Lê Văn D", "levand@gmail.com", "00000003", "00000003");
        User user5 = createTestUser("Phạm Thị E", "phamthie@gmail.com", "00000004", "00000004");
        User user6 = createTestUser("Đỗ Văn F", "dovanf@gmail.com", "00000005", "00000005");
        User user7 = createTestUser("Ngô Thị G", "ngothig@gmail.com", "00000006", "00000006");
        User user8 = createTestUser("Vũ Văn H", "vuvanh@gmail.com", "00000007", "00000007");
        User user9 = createTestUser("Bùi Thị I", "buithii@gmail.com", "00000008", "00000008");
        User user10 = createTestUser("Hoàng Văn K", "hoangvank@gmail.com", "00000009", "00000009");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(user10);

        // Act
        Sort sort = Sort.by("Id").ascending();
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("Văn", pageable);

        // Assert
        List<User> userList = allUsers.getContent();

        // Optional: print users for debugging
        for (User user : userList) {
            System.out.println(user.getFullName());
        }

        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(user1);
        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(user2);
        Assertions.assertThat(allUsers.getContent().get(2)).isEqualTo(user4);
        Assertions.assertThat(allUsers.getContent().get(3)).isEqualTo(user6);
        Assertions.assertThat(allUsers.getContent().get(4)).isEqualTo(user8);
    }

    @Test
    public void UserRepository_ExistByEmail_ReturnTrue() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        Boolean result = userRepository.existsByEmail("nguyenvana@gmail.com");

        // Assert
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void UserRepository_ExistByEmail_ReturnFalse() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn A", "nguyenvana@gmail.com", "00000000", "00000000");
        userRepository.save(user1);

        // Act
        Boolean result = userRepository.existsByEmail("nguyenvanb@gmail.com"); // Check for non-existent email

        // Assert
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void UserRepository_FindByEmail_ReturnUser() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn X", "nguyenvanx@gmail.com", "11111111", "11111111");
        userRepository.save(user1);

        // Act
        // Assuming findByEmail returns User directly, as per your commented out line.
        // It's often better to return Optional<User> for methods that might not find a result.
        User foundUser = userRepository.findByEmail("nguyenvanx@gmail.com");

        // Assert
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getEmail()).isEqualTo("nguyenvanx@gmail.com");
    }

    @Test
    public void UserRepository_FindByEmail_ReturnNull() {
        // Arrange - No user with this email
        User user1 = createTestUser("Nguyễn Văn X", "nguyenvanx@gmail.com", "11111111", "11111111");
        userRepository.save(user1);


        // Act
        User foundUser = userRepository.findByEmail("nonexistent@gmail.com");

        // Assert
        Assertions.assertThat(foundUser).isNull();
    }

    @Test
    public void UserRepository_ExistsByPhone_ReturnTrue() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn Y", "nguyenvany@gmail.com", "22222222", "22222222");
        userRepository.save(user1);

        // Act
        boolean exists = userRepository.existsByPhone("22222222");

        // Assert
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void UserRepository_ExistsByPhone_ReturnFalse() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn Y", "nguyenvany@gmail.com", "22222222", "22222222");
        userRepository.save(user1);

        // Act
        boolean exists = userRepository.existsByPhone("99999999"); // Non-existent phone

        // Assert
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    public void UserRepository_FindByEmailWithRoles_ReturnUserWithRoles() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        // Save the adminRole first!
        adminRole = roleRepository.save(adminRole); // Ensure the role is persisted and has an ID

        Role userRole = new Role();
        userRole.setName("USER");
        // Save the userRole first!
        userRole = roleRepository.save(userRole); // Ensure the role is persisted and has an ID

        User user1 = createTestUser("Nguyễn Văn Z", "nguyenvanz@gmail.com", "33333333", "33333333");
        user1.setRoles(Set.of(adminRole, userRole)); // Assign the *persisted* roles to the user
        userRepository.save(user1);

        // Act
        Optional<User> foundUserOptional = userRepository.findByEmailWithRoles("nguyenvanz@gmail.com");

        // Assert
        Assertions.assertThat(foundUserOptional).isPresent();
        User foundUser = foundUserOptional.get();
        Assertions.assertThat(foundUser.getEmail()).isEqualTo("nguyenvanz@gmail.com");
        Assertions.assertThat(foundUser.getRoles()).isNotNull();
        Assertions.assertThat(foundUser.getRoles()).hasSize(2);
        Assertions.assertThat(foundUser.getRoles()).extracting(Role::getName).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    public void UserRepository_FindByEmailWithRoles_ReturnEmptyForNonExistentEmail() {
        // Arrange - No user with this email

        // Act
        Optional<User> foundUserOptional = userRepository.findByEmailWithRoles("nonexistentwithroles@gmail.com");

        // Assert
        Assertions.assertThat(foundUserOptional).isEmpty();
    }

    @Test
    public void UserRepository_FindAuditInfoByEmail_ReturnProjection() {
        // Arrange
        User user1 = createTestUser("Nguyễn Văn Audit", "audit@example.com", "44444444", "44444444");
        userRepository.save(user1);

        // Act
        Optional<UserAuditProjection> projectionOptional = userRepository.findAuditInfoByEmail("audit@example.com");

        // Assert
        Assertions.assertThat(projectionOptional).isPresent();
        UserAuditProjection projection = projectionOptional.get();
        Assertions.assertThat(projection.getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(projection.getFullName()).isEqualTo("Nguyễn Văn Audit");
        Assertions.assertThat(projection.getEmail()).isEqualTo("audit@example.com");
        Assertions.assertThat(projection.getIdentifyNum()).isEqualTo("44444444");
    }

    @Test
    public void UserRepository_FindAuditInfoByEmail_ReturnEmptyForNonExistentEmail() {
        // Arrange - No user with this email

        // Act
        Optional<UserAuditProjection> projectionOptional = userRepository.findAuditInfoByEmail("nonexistentaudit@example.com");

        // Assert
        Assertions.assertThat(projectionOptional).isEmpty();
    }


}