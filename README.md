### server batch broadcast

클라에서 액션이 발생하면   
해당액션을 서버로 올림(단 클라도 어느정도 배치로 처리해주는게 좋음)   
서버는 해당액션을 누적하고   
정해진 스레드에서 배치방식으로 브로드캐스트   
단 클라에서 보간작업이 들어갈거기때문에 모든 액셔는에는 ts를 달아놓는다

### client-side interpolation

클라이언트는 정해진 주기마다 오는 서버의 이벤트를 수신   
액션을 받으면 ts를 기준으로 elapsedTime을 계산하고  
이전에 수신한 위치와   
지금 수신한 위치를 기반으로  
예측된 현재 위치를 계산해낸다

### deep

디테일하게 보면 고려해줄게 더 많아보이긴함