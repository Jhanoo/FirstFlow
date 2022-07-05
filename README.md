# K-Xylophone

APK 파일은 아래 링크를 이용해 주세요!

[https://drive.google.com/file/d/16v1bb4dxOHksmponfWIgEm1lsCphrnzS/view?usp=sharing](https://drive.google.com/file/d/16v1bb4dxOHksmponfWIgEm1lsCphrnzS/view?usp=sharing)

# 🙋‍♂️개발한 사람

- [정찬우](https://github.com/Jhanoo)
    - 한양대학교 컴퓨터소프트웨어학부 17학번
- [김재민](https://github.com/dev-jaemin)
    - 고려대학교 컴퓨터학과 18학번

# 🌐앱 소개

KAIST에서 진행하는 2022 여름 몰입캠프 1주차 과제물입니다.

3개 탭을 구현하는 것이 과제였으며, 구현한 기능은 아래와 같습니다.

- 연락처
- 나만의 이미지 갤러리
- 자유 과제
    - 저희 조는 실로폰 연주 및 연주를 녹음할 수 있는 기능을 개발했습니다.

앱 실행에 필요한 환경은 다음과 같습니다.

- `targetApi` : 32
- `minSdk` : 29

# 🔥세부 구현 내용

### 권한 예외처리하기

- 각 권한을 요구하는 탭은 아래와 같습니다.
    
    
    | 권한 | 사용하는 탭 |
    | --- | --- |
    | android.permission.READ_CONTACTS | 연락처 |
    | android.permission.CALL_PHONE | 연락처 |
    | android.permission.RECORD_AUDIO | 실로폰 연주, 파일 재생 |
    | android.permission.READ_EXTERNAL_STORAGE | 연락처, 나만의 갤러리, 파일 재생 |
    | android.permission.FOREGROUND_SERVICE | 실로폰 연주 |
- 처음 앱 실행 시 권한 허용 여부를 체크합니다.
- 탭은 Fragment로 구현했으며, 각 탭을 실행할 때마다 권한 여부를 체크합니다.
    - Fragment가 호출될 때마다 검사하므로, **사용자가 임의로 권한을 뺏거나 다시 주어도 에러가 발생하지 않습니다.**
- 탭을 실행하기 위한 권한이 부족하면 다음과 같은 Fragment를 띄웁니다.
    
    ![Screenshot_20220705-192527_K-Xylophone](https://user-images.githubusercontent.com/43535460/177318554-1ae7d307-b5ec-49b0-a4a0-5bf4d593816d.jpg) 
    

### Material Design Guide 적용

완벽하진 않지만, 디자인 가이드를 적용하기 위해 노력했습니다.

- 컬러 정하기
    
    | 색 종류 | 색 이름 |
    | --- | --- |
    | Primary Color | green_300(#81C784) |
    | Primary Color (Variant) | green_100(#C8E6C9) |
    | Secondary Color | brown_400(#8D6E63) |
    
    실로폰 탭과 어울리는 색을 찾기 위해 노력했고, 위와 같은 색을 주요 색으로 정했습니다.
    
    각 색깔은 역할에 따라 다양한 곳에서 사용됩니다.
    
- 하단 네비게이션
    - Material 스타일 아이콘을 사용했습니다.
    - 현재 탭은 칠해진 아이콘, 그 외에는 테두리 아이콘을 적용했습니다.

### Github 버전관리

- 현업처럼 Github를 사용하기 위해 노력했습니다.
    - 기능별 feature branch 생성 및 작업
    - main에 merge할 때 `Github Pull Request` 기능 활용하여 코드 리뷰

---

다음은 각 탭 구현 내용입니다.

## 실로폰 연주 탭

### 핵심 기능

- 건반 클릭으로 연주 가능(SoundPool 사용)
    - 터치 시 음계에 따라 다른 색깔로 0.2초 동안 표시
    - 터치 시 해당 음계의 실로폰 사운드 재생
- **연주 내용 녹음**
    - ▶ 버튼을 터치 시 녹화 또는 전송에 대한 사용자의 승인을 요청하는 prompt창이 팝업되며, 이를 승인하면 녹음이 시작되고 ■ 버튼으로 바뀝니다. (거절할 경우 녹음되지 않으며, 승인이 필요하다는 Toast가 발생됩니다.) 
    - 녹음 시작 후 ■ 버튼을 터치 시 해당 녹음이 끝나고 ▶ 버튼으로 바뀌며 연주 내용이 외부저장소에 pcm파일로 저장됩니다.
    - 저장된 pcm파일은 “재생”탭에서 확인가능합니다.
    
    - 사전 요구 사항
        - [`Manifest.permission.RECORD_AUDIO`](https://developer.android.com/reference/android/Manifest.permission?hl=ko#RECORD_AUDIO) - 오디오 녹음을 위한 Permission
        - [`MediaProjectionManager.createScreenCaptureIntent()`](https://developer.android.com/reference/android/media/projection/MediaProjectionManager?hl=ko#createScreenCaptureIntent()) -  prompt를 통해 user의 승인 필요
    - MediaProjectionManager를 통해 AudioCapture를 위한 ForegroundService 사용 권한을 받는다.
    - AudioCaptureService.java의 서비스를 ForegroundService로 실행하여
    - AudioCaptureService → ForegroundService를 통해 실행
        - NotificationManager를 통해 NotificationChannel을 생성
        - [`AudioPlaybackCaptureConfiguration.Builder`](https://developer.android.com/reference/android/media/AudioPlaybackCaptureConfiguration) 객체를 생성하여 Media 오디오(without voice)가 녹음되도록 세팅
        - AudioFormat 객체를 아래 format과 같이 세팅 (SAMPLING_RATE = 8000)
        
        ```java
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLING_RATE)
                .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                .build();
        ```
        
        - AudioRecord 객체를 생성하여 buffer 사이즈를 설정하고 위의 format을 적용 후 녹음 시작
        - Thread를 Overriding하여 출력파일 생성 및 녹음한 데이터를 외부저장소에 저장

### 스크린샷

![Screenshot_20220705-193030_K-Xylophone](https://user-images.githubusercontent.com/43535460/177313161-0a74a50a-e9e4-4474-95e6-6c220ffc3681.jpg)

## 녹음 파일 재생 탭

### 핵심 기능

- 앱 내부 저장소에서 .pcm 파일 리스트를 불러옵니다.
- 각 파일 클릭 시 스레드를 새로 생성하고, 음원 파일을 재생합니다.
    - 다른 액션을 하면 음원을 재생하고 있는 스레드는 종료되도록 개발했습니다.
        - Thread를 새로 생성하지 않으면, 재생 중에 클릭 이벤트를 받을 수 없습니다. 
        - 인터럽트를 발생시켜 종료할 수 있도록 새로운 스레드가 필요합니다.
    - 연주를 녹음할 때의 음원 세팅을 바탕으로 `AudioTrack`, `DataInputStream` 를 설정하여 재생합니다.
- 각 파일은 이름 수정 및 삭제가 가능합니다.

### 스크린샷

![Screenshot_20220705-193433_K-Xylophone](https://user-images.githubusercontent.com/43535460/177313178-06f925cf-2a02-42a4-bcd0-2091b9937586.jpg)

## 연락처 탭

### 핵심 기능

- 휴대폰 주소록과 연동(`ContactsContract`, `RecyclerView` 사용)
    - 이름
    - 연락처
    - 프로필 사진(원본 사진)
- 이름 검색
- 연락처 추가 기능
- 연락처 상세 페이지
    - 전화 걸기
    - 문자 보내기

### 스크린샷

![Screenshot_20220705-192752_K-Xylophone](https://user-images.githubusercontent.com/43535460/177313196-4d61e9e6-0d6c-44bd-bcd3-9cbfeff985ec.jpg)

## 나만의 갤러리 탭

### 핵심 기능

- 사용자가 원하는 사진을 따로 저장할 수 있는 갤러리
    - 내부 저장소에 이미지 파일 리스트 저장 및 삭제(URI, JSON 파일 형식 이용)
    - `RecyclerView`, `GridLayout` 활용하여 레이아웃 구현
- 사진 클릭 시 해당 사진 팝업 및 확대 가능(`glide` 라이브러리 활용)

### 스크린샷

![Screenshot_20220705-193017_K-Xylophone](https://user-images.githubusercontent.com/43535460/177313205-01206fdc-665e-48aa-b014-2bf58bd6ecfd.jpg)
