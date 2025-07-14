package com.example.user.repository;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.User;
//import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        //Arrange
        User newUser = new User();
        newUser.setFullName("Nguyễn Văn A");
        newUser.setEmail("nguyenvana@gmail.com");
        newUser.setPhone("0123456789");
        newUser.setIdentifyNum("0123456789");
        newUser.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        newUser.setGender("Nam");
        newUser.setPassword("123123123");
        newUser.setAge(32);
        newUser.setDate_of_Birth("1/1/1992");
        newUser.setCreate_at(LocalDateTime.now());
        newUser.setUpdate_at(LocalDateTime.now());

        //Act
        User savedUser = userRepository.save(newUser);


        //Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_GetAll_ReturnAllUsers() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        User User2 = new User();
        User1.setFullName("Nguyễn Văn B");
        User1.setEmail("nguyenvanb@gmail.com");
        User1.setPhone("00000001");
        User1.setIdentifyNum("00000001");
        User1.setAddress("12B Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nữ");
        User1.setPassword("123123123");
        User1.setAge(33);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);
        userRepository.save(User2);

        //Act
        List<User> allUsers = userRepository.findAll();

        //Assert
        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_GetAll_ReturnEmpty() {
        //Arrange

        //Act
        List<User> allUsers = userRepository.findAll();

        //Assert
        Assertions.assertThat(allUsers).isEmpty();
        Assertions.assertThat(allUsers.size()).isEqualTo(0);
    }

    @Test
    public void UserRepository_FindById_ReturnUser() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        User resultUser = userRepository.findById(User1.getId()).orElseThrow(()-> new ResourceNotFoundException("User","Id",User1.getId()));

        //Assert
        Assertions.assertThat(resultUser).isNotNull();
    }

    @Test
    public void UserRepository_FindById_ReturnEmpty() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        Optional<User> resultUser = userRepository.findById(User1.getId()+1);

        //Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_FindByPhone_ReturnUser() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        User resultUser = userRepository.findByPhone(User1.getPhone()).orElseThrow(()-> new ResourceNotFoundException("User","Phone",User1.getPhone()));

        //Assert
        Assertions.assertThat(resultUser).isNotNull();
    }

    @Test
    public void UserRepository_FindByPhone_ReturnEmpty() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        Optional<User> resultUser = userRepository.findByPhone("00000001");

        //Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_UpdateUser_ReturnUser() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        User resultUser = userRepository.findById(User1.getId()).orElseThrow(()-> new ResourceNotFoundException("User","Id",User1.getId()));
        resultUser.setAge(resultUser.getAge()+1);
        resultUser.setFullName("Nguyễn Văn C");

        User UpdateUser = userRepository.save(resultUser);

        //Assert
        Assertions.assertThat(UpdateUser).isNotNull();
        Assertions.assertThat(UpdateUser.getId()).isEqualTo(User1.getId());
        Assertions.assertThat(UpdateUser.getFullName()).isEqualTo("Nguyễn Văn C");
        Assertions.assertThat(UpdateUser.getAge()).isEqualTo(33);
    }

    @Test
    public void UserRepository_DeleteUser_ReturnEmpty() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        userRepository.deleteById(User1.getId());
        Optional<User> resultUser = userRepository.findById(User1.getId());

        //Assert
        Assertions.assertThat(resultUser).isEmpty();
    }

    @Test
    public void UserRepository_DeleteUserNotFound_ReturnDbUnchange() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        userRepository.deleteById(User1.getId()+1);
        Optional<User> resultUser = userRepository.findById(User1.getId());

        //Assert
        Assertions.assertThat(resultUser).isNotNull();
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSingleMatchUser() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        User User2 = new User();
        User2.setFullName("Nguyễn Văn B");
        User2.setEmail("nguyenvanb@gmail.com");
        User2.setPhone("00000001");
        User2.setIdentifyNum("00000001");
        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
        User2.setGender("Nam");
        User2.setPassword("123123123");
        User2.setAge(33);
        User2.setDate_of_Birth("2/2/1991");
        User2.setCreate_at(LocalDateTime.now());
        User2.setUpdate_at(LocalDateTime.now());

        User User3 = new User();
        User3.setFullName("Trần Thị C");
        User3.setEmail("tranthic@gmail.com");
        User3.setPhone("00000002");
        User3.setIdentifyNum("00000002");
        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
        User3.setGender("Nữ");
        User3.setPassword("123123123");
        User3.setAge(28);
        User3.setDate_of_Birth("3/3/1996");
        User3.setCreate_at(LocalDateTime.now());
        User3.setUpdate_at(LocalDateTime.now());

        User User4 = new User();
        User4.setFullName("Lê Văn D");
        User4.setEmail("levand@gmail.com");
        User4.setPhone("00000003");
        User4.setIdentifyNum("00000003");
        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
        User4.setGender("Nam");
        User4.setPassword("123123123");
        User4.setAge(40);
        User4.setDate_of_Birth("4/4/1984");
        User4.setCreate_at(LocalDateTime.now());
        User4.setUpdate_at(LocalDateTime.now());

        User User5 = new User();
        User5.setFullName("Phạm Thị E");
        User5.setEmail("phamthie@gmail.com");
        User5.setPhone("00000004");
        User5.setIdentifyNum("00000004");
        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
        User5.setGender("Nữ");
        User5.setPassword("123123123");
        User5.setAge(25);
        User5.setDate_of_Birth("5/5/1999");
        User5.setCreate_at(LocalDateTime.now());
        User5.setUpdate_at(LocalDateTime.now());

        User User6 = new User();
        User6.setFullName("Đỗ Văn F");
        User6.setEmail("dovanf@gmail.com");
        User6.setPhone("00000005");
        User6.setIdentifyNum("00000005");
        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
        User6.setGender("Nam");
        User6.setPassword("123123123");
        User6.setAge(36);
        User6.setDate_of_Birth("6/6/1988");
        User6.setCreate_at(LocalDateTime.now());
        User6.setUpdate_at(LocalDateTime.now());

        User User7 = new User();
        User7.setFullName("Ngô Thị G");
        User7.setEmail("ngothig@gmail.com");
        User7.setPhone("00000006");
        User7.setIdentifyNum("00000006");
        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
        User7.setGender("Nữ");
        User7.setPassword("123123123");
        User7.setAge(30);
        User7.setDate_of_Birth("7/7/1994");
        User7.setCreate_at(LocalDateTime.now());
        User7.setUpdate_at(LocalDateTime.now());

        User User8 = new User();
        User8.setFullName("Vũ Văn H");
        User8.setEmail("vuvanh@gmail.com");
        User8.setPhone("00000007");
        User8.setIdentifyNum("00000007");
        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
        User8.setGender("Nam");
        User8.setPassword("123123123");
        User8.setAge(27);
        User8.setDate_of_Birth("8/8/1997");
        User8.setCreate_at(LocalDateTime.now());
        User8.setUpdate_at(LocalDateTime.now());

        User User9 = new User();
        User9.setFullName("Bùi Thị I");
        User9.setEmail("buithii@gmail.com");
        User9.setPhone("00000008");
        User9.setIdentifyNum("00000008");
        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
        User9.setGender("Nữ");
        User9.setPassword("123123123");
        User9.setAge(29);
        User9.setDate_of_Birth("9/9/1995");
        User9.setCreate_at(LocalDateTime.now());
        User9.setUpdate_at(LocalDateTime.now());

        User User10 = new User();
        User10.setFullName("Hoàng Văn K");
        User10.setEmail("hoangvank@gmail.com");
        User10.setPhone("00000009");
        User10.setIdentifyNum("00000009");
        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
        User10.setGender("Nam");
        User10.setPassword("123123123");
        User10.setAge(35);
        User10.setDate_of_Birth("10/10/1989");
        User10.setCreate_at(LocalDateTime.now());
        User10.setUpdate_at(LocalDateTime.now());


        userRepository.save(User1);
        userRepository.save(User2);
        userRepository.save(User3);
        userRepository.save(User4);
        userRepository.save(User5);
        userRepository.save(User6);
        userRepository.save(User7);
        userRepository.save(User8);
        userRepository.save(User9);
        userRepository.save(User10);


        //Act
        List<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("Nguyễn Văn A");

        //Assert
        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.size()).isEqualTo(1);
        Assertions.assertThat(allUsers.get(0)).isEqualTo(User1);
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnMultipleMatchUsers() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        User User2 = new User();
        User2.setFullName("Nguyễn Văn B");
        User2.setEmail("nguyenvanb@gmail.com");
        User2.setPhone("00000001");
        User2.setIdentifyNum("00000001");
        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
        User2.setGender("Nam");
        User2.setPassword("123123123");
        User2.setAge(33);
        User2.setDate_of_Birth("2/2/1991");
        User2.setCreate_at(LocalDateTime.now());
        User2.setUpdate_at(LocalDateTime.now());

        User User3 = new User();
        User3.setFullName("Trần Thị C");
        User3.setEmail("tranthic@gmail.com");
        User3.setPhone("00000002");
        User3.setIdentifyNum("00000002");
        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
        User3.setGender("Nữ");
        User3.setPassword("123123123");
        User3.setAge(28);
        User3.setDate_of_Birth("3/3/1996");
        User3.setCreate_at(LocalDateTime.now());
        User3.setUpdate_at(LocalDateTime.now());

        User User4 = new User();
        User4.setFullName("Lê Văn D");
        User4.setEmail("levand@gmail.com");
        User4.setPhone("00000003");
        User4.setIdentifyNum("00000003");
        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
        User4.setGender("Nam");
        User4.setPassword("123123123");
        User4.setAge(40);
        User4.setDate_of_Birth("4/4/1984");
        User4.setCreate_at(LocalDateTime.now());
        User4.setUpdate_at(LocalDateTime.now());

        User User5 = new User();
        User5.setFullName("Phạm Thị E");
        User5.setEmail("phamthie@gmail.com");
        User5.setPhone("00000004");
        User5.setIdentifyNum("00000004");
        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
        User5.setGender("Nữ");
        User5.setPassword("123123123");
        User5.setAge(25);
        User5.setDate_of_Birth("5/5/1999");
        User5.setCreate_at(LocalDateTime.now());
        User5.setUpdate_at(LocalDateTime.now());

        User User6 = new User();
        User6.setFullName("Đỗ Văn F");
        User6.setEmail("dovanf@gmail.com");
        User6.setPhone("00000005");
        User6.setIdentifyNum("00000005");
        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
        User6.setGender("Nam");
        User6.setPassword("123123123");
        User6.setAge(36);
        User6.setDate_of_Birth("6/6/1988");
        User6.setCreate_at(LocalDateTime.now());
        User6.setUpdate_at(LocalDateTime.now());

        User User7 = new User();
        User7.setFullName("Ngô Thị G");
        User7.setEmail("ngothig@gmail.com");
        User7.setPhone("00000006");
        User7.setIdentifyNum("00000006");
        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
        User7.setGender("Nữ");
        User7.setPassword("123123123");
        User7.setAge(30);
        User7.setDate_of_Birth("7/7/1994");
        User7.setCreate_at(LocalDateTime.now());
        User7.setUpdate_at(LocalDateTime.now());

        User User8 = new User();
        User8.setFullName("Vũ Văn H");
        User8.setEmail("vuvanh@gmail.com");
        User8.setPhone("00000007");
        User8.setIdentifyNum("00000007");
        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
        User8.setGender("Nam");
        User8.setPassword("123123123");
        User8.setAge(27);
        User8.setDate_of_Birth("8/8/1997");
        User8.setCreate_at(LocalDateTime.now());
        User8.setUpdate_at(LocalDateTime.now());

        User User9 = new User();
        User9.setFullName("Bùi Thị I");
        User9.setEmail("buithii@gmail.com");
        User9.setPhone("00000008");
        User9.setIdentifyNum("00000008");
        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
        User9.setGender("Nữ");
        User9.setPassword("123123123");
        User9.setAge(29);
        User9.setDate_of_Birth("9/9/1995");
        User9.setCreate_at(LocalDateTime.now());
        User9.setUpdate_at(LocalDateTime.now());

        User User10 = new User();
        User10.setFullName("Hoàng Văn K");
        User10.setEmail("hoangvank@gmail.com");
        User10.setPhone("00000009");
        User10.setIdentifyNum("00000009");
        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
        User10.setGender("Nam");
        User10.setPassword("123123123");
        User10.setAge(35);
        User10.setDate_of_Birth("10/10/1989");
        User10.setCreate_at(LocalDateTime.now());
        User10.setUpdate_at(LocalDateTime.now());


        userRepository.save(User1);
        userRepository.save(User2);
        userRepository.save(User3);
        userRepository.save(User4);
        userRepository.save(User5);
        userRepository.save(User6);
        userRepository.save(User7);
        userRepository.save(User8);
        userRepository.save(User9);
        userRepository.save(User10);


        //Act
        Sort sort = Sort.by("fullName").ascending();
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("nguyễn", pageable);

        //Assert
        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(User1);
        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(User2);
    }

    @Test
    public void UserRepository_FindByFullNameContainingIgnoreCase_ReturnSelectedNumberOfMatchUsers() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, Quận 10, TP.HCM");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        User User2 = new User();
        User2.setFullName("Nguyễn Văn B");
        User2.setEmail("nguyenvanb@gmail.com");
        User2.setPhone("00000001");
        User2.setIdentifyNum("00000001");
        User2.setAddress("12B Lý Thường Kiệt, Quận 10, TP.HCM");
        User2.setGender("Nam");
        User2.setPassword("123123123");
        User2.setAge(33);
        User2.setDate_of_Birth("2/2/1991");
        User2.setCreate_at(LocalDateTime.now());
        User2.setUpdate_at(LocalDateTime.now());

        User User3 = new User();
        User3.setFullName("Trần Thị C");
        User3.setEmail("tranthic@gmail.com");
        User3.setPhone("00000002");
        User3.setIdentifyNum("00000002");
        User3.setAddress("15 Nguyễn Trãi, Quận 5, TP.HCM");
        User3.setGender("Nữ");
        User3.setPassword("123123123");
        User3.setAge(28);
        User3.setDate_of_Birth("3/3/1996");
        User3.setCreate_at(LocalDateTime.now());
        User3.setUpdate_at(LocalDateTime.now());

        User User4 = new User();
        User4.setFullName("Lê Văn D");
        User4.setEmail("levand@gmail.com");
        User4.setPhone("00000003");
        User4.setIdentifyNum("00000003");
        User4.setAddress("22 Pasteur, Quận 1, TP.HCM");
        User4.setGender("Nam");
        User4.setPassword("123123123");
        User4.setAge(40);
        User4.setDate_of_Birth("4/4/1984");
        User4.setCreate_at(LocalDateTime.now());
        User4.setUpdate_at(LocalDateTime.now());

        User User5 = new User();
        User5.setFullName("Phạm Thị E");
        User5.setEmail("phamthie@gmail.com");
        User5.setPhone("00000004");
        User5.setIdentifyNum("00000004");
        User5.setAddress("101 Trần Hưng Đạo, Quận 1, TP.HCM");
        User5.setGender("Nữ");
        User5.setPassword("123123123");
        User5.setAge(25);
        User5.setDate_of_Birth("5/5/1999");
        User5.setCreate_at(LocalDateTime.now());
        User5.setUpdate_at(LocalDateTime.now());

        User User6 = new User();
        User6.setFullName("Đỗ Văn F");
        User6.setEmail("dovanf@gmail.com");
        User6.setPhone("00000005");
        User6.setIdentifyNum("00000005");
        User6.setAddress("88 Lê Lợi, Quận 3, TP.HCM");
        User6.setGender("Nam");
        User6.setPassword("123123123");
        User6.setAge(36);
        User6.setDate_of_Birth("6/6/1988");
        User6.setCreate_at(LocalDateTime.now());
        User6.setUpdate_at(LocalDateTime.now());

        User User7 = new User();
        User7.setFullName("Ngô Thị G");
        User7.setEmail("ngothig@gmail.com");
        User7.setPhone("00000006");
        User7.setIdentifyNum("00000006");
        User7.setAddress("45 Võ Thị Sáu, Quận 3, TP.HCM");
        User7.setGender("Nữ");
        User7.setPassword("123123123");
        User7.setAge(30);
        User7.setDate_of_Birth("7/7/1994");
        User7.setCreate_at(LocalDateTime.now());
        User7.setUpdate_at(LocalDateTime.now());

        User User8 = new User();
        User8.setFullName("Vũ Văn H");
        User8.setEmail("vuvanh@gmail.com");
        User8.setPhone("00000007");
        User8.setIdentifyNum("00000007");
        User8.setAddress("10 Phan Xích Long, Phú Nhuận, TP.HCM");
        User8.setGender("Nam");
        User8.setPassword("123123123");
        User8.setAge(27);
        User8.setDate_of_Birth("8/8/1997");
        User8.setCreate_at(LocalDateTime.now());
        User8.setUpdate_at(LocalDateTime.now());

        User User9 = new User();
        User9.setFullName("Bùi Thị I");
        User9.setEmail("buithii@gmail.com");
        User9.setPhone("00000008");
        User9.setIdentifyNum("00000008");
        User9.setAddress("2A Cách Mạng Tháng Tám, Quận 10, TP.HCM");
        User9.setGender("Nữ");
        User9.setPassword("123123123");
        User9.setAge(29);
        User9.setDate_of_Birth("9/9/1995");
        User9.setCreate_at(LocalDateTime.now());
        User9.setUpdate_at(LocalDateTime.now());

        User User10 = new User();
        User10.setFullName("Hoàng Văn K");
        User10.setEmail("hoangvank@gmail.com");
        User10.setPhone("00000009");
        User10.setIdentifyNum("00000009");
        User10.setAddress("6 Nguyễn Huệ, Quận 1, TP.HCM");
        User10.setGender("Nam");
        User10.setPassword("123123123");
        User10.setAge(35);
        User10.setDate_of_Birth("10/10/1989");
        User10.setCreate_at(LocalDateTime.now());
        User10.setUpdate_at(LocalDateTime.now());


        userRepository.save(User1);
        userRepository.save(User2);
        userRepository.save(User3);
        userRepository.save(User4);
        userRepository.save(User5);
        userRepository.save(User6);
        userRepository.save(User7);
        userRepository.save(User8);
        userRepository.save(User9);
        userRepository.save(User10);


        //Act
        Sort sort = Sort.by("Id").ascending();
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<User> allUsers = userRepository.findByFullNameContainingIgnoreCase("Văn", pageable);

        //Assert

        List<User> userList = allUsers.getContent();

        for (User user : userList) {
            System.out.println(user);
        }

        Assertions.assertThat(allUsers).isNotNull();
        Assertions.assertThat(allUsers.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(allUsers.getContent().get(0)).isEqualTo(User1);
        Assertions.assertThat(allUsers.getContent().get(1)).isEqualTo(User2);
        Assertions.assertThat(allUsers.getContent().get(2)).isEqualTo(User4);
        Assertions.assertThat(allUsers.getContent().get(3)).isEqualTo(User6);
        Assertions.assertThat(allUsers.getContent().get(4)).isEqualTo(User8);
    }

    @Test
    public void UserRepository_ExistByEmail_ReturnTrue() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        Boolean result = userRepository.existsByEmail("nguyenvana@gmail.com");

        //Assert
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void UserRepository_ExistByEmail_ReturnTFalse() {
        //Arrange
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        userRepository.save(User1);

        //Act
        Boolean result = userRepository.existsByEmail("nguyenvanb@gmail.com");

        //Assert
        Assertions.assertThat(result).isFalse();
    }

}
