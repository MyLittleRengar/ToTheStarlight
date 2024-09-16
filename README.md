# 2024 ToTheStarlight 프로젝트
2024.08.19 ~ 2024.09.16<br>
천문과 달에 관심이 많았는데 우연히 공공데이터포털을 둘러보던 중 한국천문연구원에서 여러 API를 제공하는 것을 보았다. 그 중에서 월령 정보, 천문 현상, 출몰 시각을 이용하여 월령, 일출-몰 월출-몰, 달마다 일어나는 천문현상의 정보를 앱으로 간편하게 보고싶어 만들게 되었다.<br>

화면<br>
<img width="1104" alt="image" src="https://github.com/user-attachments/assets/8f84251b-4460-4e7c-a505-c7192e52014e"><br><br>

개발
* Android Studio API 34
* Kotlin   
* 공공데이터 API(한국천문연구원_월령 정보, 한국천문연구원_천문현상 정보, 한국천문연구원_출몰시각 정보)

한국천문연구원 API는 XML, JSON 두가지의 파일타입을 지원한다. 그 중에 XML타입으로 선택했다.  XML을 파싱하기 위해서 각각의 item 태그 별로 저장하였다. 
XML파싱이랑 리사이클러뷰 아이템 추가, 캘린더에 달의 월령 이미지를 추가하는 작업은 모두 비동기처리를 하였다.

리사이클러뷰의 아이템을 길게 누르면 알림 설정을 할 수 있고, 짧게 누르면 자동으로 구글 검색창에 해당 천문 이벤트를 검색할 수 있게 해두었다.
알림을 설정하면 천문 이벤트가 발생하는 날 00:05분에 알림이 가게 설정했다.

