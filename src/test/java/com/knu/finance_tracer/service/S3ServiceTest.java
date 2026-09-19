package com.knu.finance_tracer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "region", "eu-north-1");
    }

    @Test
    void uploadFile_WhenFileIsNull_ShouldReturnNull() {
        assertNull(s3Service.uploadFile(null));
    }

    @Test
    void uploadFile_WhenFileIsEmpty_ShouldReturnNull() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        assertNull(s3Service.uploadFile(emptyFile));
    }

    @Test
    void uploadFile_Success_ShouldReturnUrl() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());

        String url = s3Service.uploadFile(file);

        assertNotNull(url);
        assertTrue(url.contains("https://test-bucket.s3.eu-north-1.amazonaws.com/"));
        assertTrue(url.contains("test.jpg"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFile_WhenIoExceptionOccurs_ShouldThrowRuntimeException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenThrow(new IOException("Disk error"));

        assertThrows(RuntimeException.class, () -> s3Service.uploadFile(file));
    }
}
