# ARTISTACK-Server

<h1 align="center">

<img src="https://raw.githubusercontent.com/umc-artistack/artistack-server/main/readme/logo.png" width=700/>

<div align="center">

[![GitHub Open Issues](https://img.shields.io/github/issues-raw/umc-artistack/artistack-server?color=green)](https://github.com/umc-artistack/artistack-server/issues)
[![GitHub Closed Issues](https://img.shields.io/github/issues-closed-raw/umc-artistack/artistack-server?color=red)](https://github.com/umc-artistack/artistack-server/issues?q=is%3Aissue+is%3Aclosed)
[![GitHub Open PR](https://img.shields.io/github/issues-pr-raw/umc-artistack/artistack-server?color=green)](https://github.com/umc-artistack/artistack-server/pulls)
[![GitHub Closed PR](https://img.shields.io/github/issues-pr-closed-raw/umc-artistack/artistack-server?color=red)](https://github.com/umc-artistack/artistack-server/pulls?q=is%3Apr+is%3Aclosed)

</div>

</h1>


## Artistack

>**`Artistack`** 은 다양한 뮤지션이 만나 새로운 음악을 만들어내는 숏폼 SNS입니다.

<br/>

### ✅ 간단한 로그인
- 카카오톡 계정과 애플 계정을 사용하여 간편하게 로그인할 수 있어요.

### ✅ 메인 페이지
- 스와이프 기능을 통해 다양한 프로젝트를 무한히 감상할 수 있어요.
 - 좋아요와 스택 기능을 사용하여 뮤지션과 소통할 수 있어요.

### ✅ 스택
- 마음에 드는 연주를 발견하셨다면, 스택 버튼을 눌러 뮤지션과의 협주를 시작할 수 있어요.

### ✅ 협주 (녹화)
- 타이머, 화면전환 기능을 활용하여 연주 영상을 촬영하고 음원의 볼륨을 조절하여 영상을 완성해보세요.
- 영상 촬영을 마친 후 곡제목, 설명, 곡정보를 입력하여 동영상을 업로드할 수 있어요.

### ✅ 마이페이지
- 내가 시작하거나 스택한 프로젝트의 조회수, 좋아요수, 스택수를 한눈에 볼 수 있어요.

<br>
<br>

## 🎬 Demo Video
<div align=center>
<a href="https://www.youtube.com/watch?v=Nnv-BlhFHlY"><img width="700" alt="image" src="https://user-images.githubusercontent.com/54897403/209075719-91ae831e-3b4a-47c2-82b0-9264915dc196.png"></a>
</div>

<br>

### [🗒️ ARTISTACK Notion](https://www.notion.so/ARTISTACK-ae9600707aff4304872610760b0e3411)

### [😺 iOS Repository](https://github.com/umc-artistack/artistack-client)

<br>

## 멤버
| **이정연** |  **김명승** |  **우다현**  |
| :-------------------------------------------------------:| :-------------------------------------------------------: | :-------------------------------------------------------: |
| ![](https://avatars.githubusercontent.com/u/65899774?v=4) | ![](https://avatars.githubusercontent.com/u/54897403?v=4) |  ![](https://avatars.githubusercontent.com/u/60066586?v=4) | ![](https://avatars.githubusercontent.com/u/60066586?v=4) |
|      <a href="https://github.com/leeeeeyeon"><img src="https://img.shields.io/badge/leeeeeyeon-655ced?style=social&logo=github"/></a>       | <a href="https://github.com/mskim9967"><img src="https://img.shields.io/badge/mskim9967-655ced?style=social&logo=github"/></a>          |     <a href="https://github.com/DahyeonWoo"><img src="https://img.shields.io/badge/DahyeonWoo-655ced?style=social&logo=github"/></a>      |


## Project Architecture
![image](https://user-images.githubusercontent.com/54897403/209074574-9083de13-002a-461c-8249-6b1b49bc7539.png)


## 🗃 Project Folder

```
artistack-server
├─ 📁.github
│  └─ 📁 workflows
├─ .gitignore
├─ gradle
├─ gradlew
├─ gradlew.bat
└─ 📁 src
   ├─ 📁 main
   │  ├─ java.com.artistack
   │  │  ├─ ArtistackApplication.java
   │  │  ├─ 📁 base
   │  │  │  ├─ 📁 constant
   │  │  │  └─ 📁 dto
   │  │  ├─ 📁 config
   │  │  ├─ 📁 controller
   │  │  ├─ 📁 instrument
   │  │  │  ├─ 📁 controller
   │  │  │  ├─ 📁 domain
   │  │  │  ├─ 📁 dto
   │  │  │  ├─ 📁 repository
   │  │  │  └─ 📁 service
   │  │  ├─ 📁 jwt
   │  │  │  ├─ 📁 domain
   │  │  │  ├─ 📁 dto
   │  │  │  ├─ 📁 repository
   │  │  │  └─ 📁 service
   │  │  ├─ 📁 oauth
   │  │  │  ├─ 📁 constant
   │  │  │  ├─ 📁 controller
   │  │  │  ├─ 📁 domain
   │  │  │  ├─ 📁 dto
   │  │  │  ├─ 📁 repository
   │  │  │  └─ 📁 service
   │  │  ├─ 📁 project
   │  │  │  ├─ 📁 constant
   │  │  │  ├─ 📁 controller
   │  │  │  ├─ 📁 domain
   │  │  │  ├─ 📁 dto
   │  │  │  ├─ 📁 repository
   │  │  │  └─ 📁 service
   │  │  ├─ 📁 upload
   │  │  │  ├─ 📁 controller
   │  │  │  ├─ 📁 dto
   │  │  │  └─ 📁 service
   │  │  ├─ 📁 user
   │  │  │  ├─ 📁 constant
   │  │  │  ├─ 📁 controller
   │  │  │  ├─ 📁 domain
   │  │  │  ├─ 📁 dto
   │  │  │  ├─ 📁 repository
   │  │  │  └─ 📁 service
   │  │  └─ 📁 util
   └─ 📁 test
      ├─ 📁 java.com.artistack
      │  ├─ ArtistackApplicationTests.java
      │  ├─ 📁 config
      │  ├─ 📁 controller
      │  └─ 📁 service
      └─ 📁 resources
```
