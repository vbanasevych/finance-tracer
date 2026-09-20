package com.knu.finance_tracer.service;

import com.knu.finance_tracer.entity.User;
import com.knu.finance_tracer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldReturnSavedUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(new User());

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertNotNull(found);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        User existingUser = new User();
        existingUser.setId(1L);

        User userDetails = new User();
        userDetails.setName("New Name");
        userDetails.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, userDetails);

        assertEquals("New Name", existingUser.getName());
        assertEquals("new@test.com", existingUser.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser_ShouldCallDeleteById() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
