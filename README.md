![Badge](https://img.shields.io/badge/version-1.0.0-success.svg)
# os-process-scheduling-simulator
2022-1학기 KOREATECH 운영체제 프로세스 스케쥴링 알고리즘 과제  
Instructor: Duksu Kim

<p align="center">
   <img width="75%" alt="스크린샷 2022-05-05 오후 10 37 42" src="https://user-images.githubusercontent.com/23609119/166935181-2ea9d4f1-f524-4cd8-b77c-e09cc9c020f6.png">
   <img width="75%" alt="스크린샷 2022-05-05 오후 10 39 25" src="https://user-images.githubusercontent.com/23609119/166935502-9b5914c6-94a4-4698-a336-91d0890ba1b8.png">
   <img width="75%" alt="May-05-2022 23-50-47" src="https://user-images.githubusercontent.com/23609119/166950620-f22e314e-af22-4342-8e11-91e63dfe624c.gif">


<table>
   <tr>
      <td>
         <img alt="스크린샷 2022-05-05 오후 10 48 28" src="https://user-images.githubusercontent.com/23609119/166937358-4f6d3940-d30a-4d85-9500-9b3bc78578d3.png">
      </td>
      <td>
         <img alt="스크린샷 2022-05-05 오후 10 48 41" src="https://user-images.githubusercontent.com/23609119/166937404-73ebb7d6-c327-4a6e-a8bf-cd0b0b9d3db0.png">
      </td>
   </tr>
   <tr>
      <td align="center">Random Process Generator</td>
      <td align="center">About Screen</td>
   </tr>
  </table>
</p>


## Team Roles

**Ahwi Lee**: PL, Project report/presentation  
**Heekwon Kang**: Project report/presentation, Our own algorithm  
**Jaeyong Kim**: PM, Scheduling Algorithm, Our own algorithm  
**Seungmin Yang**: GUI, Scheduling Algorithm

## Language and Open Source Libraries

### Language

Kotlin 1.6.10 - 100% 💯

### Build Tool

Gradle 7.2  
https://github.com/gradle/gradle

###  Open source libraries

1. Compose Multiplatform, by JetBrains 1.1.1  
   https://github.com/JetBrains/compose-jb
2. Kotlin multiplatform / multi-format reflectionless serialization 1.3.2/1.6.10  
   https://github.com/Kotlin/kotlinx.serialization

## Class Diagram

### GUI
#### Windows
![uml_main](https://user-images.githubusercontent.com/23609119/166937237-6db09b68-53cb-4e63-8e8b-923973309e67.png)
#### Scheduling Simulator Main Screen
![uml_compose](https://user-images.githubusercontent.com/23609119/166935090-26868219-9e32-4622-bd5d-dcdacca27ea1.png)
TopBarKt, ProcessKt, CoreKt, ResultTableKt, ReadyQueueChartKt, GanttChartKt 파일은 실제 GUI의 각 영역별로 배치되어 있습니다.

### Scheduling Algorithm
![uml_schedulingalgorithm](https://user-images.githubusercontent.com/23609119/166936696-58ec846b-4356-45ce-92b9-4cd1e4750bb5.png)
#### Basic 5 Scheduling Algorithms
1. FCFS
2. RR(Round Robin, RR Quantum can be changed)
3. SPN(Shortest Process Next)
4. SRTN(Shortest Remaining Time Next)
5. HRRN

#### Custom Scheduling Algorithm
* 가정
 1. 프로세서는 P-Core와 E-Core만 가질 수 있다.
 2. 프로세스의 workload, arrival time 정보를 알고 있다.
* 목적 : P-Core를 최대한 이용하여 입력받은 프로세스를 빠른 시간내에 끝내면서, E-Core를 이용하여 P-Core의 전력낭비를 줄이는 알고리즘을 구현한다.
* 작동 방식


### System Properties
1. P-Core, E-Core
2. E-Core는 1초에 1의 일을 처리, 1초에 전력 1W 소모
3. P-Core는 1초의 2의 일을 처리, 1초의 전력 3W 소모
4. P-Core, E-Core의 대기 전력은 0.1W
5. 1초 단위로 이루어지는 Scheduling -> P-Core에 할당된 작업의 남은 일의 양이 1이어도, 1초와 3W를 소모

## Usage
### Process
원하는 프로세스명, Arrival Time, Workload를 입력 후 Add 버튼을 눌러 프로세스를 추가할 수 있습니다.  
Export to.. 기능으로 현재 프로세스의 상태를 json 파일로 내보낼 수 있으면 Import from.. 기능으로 json 파일로부터 프로세스의 상태를 불러올 수 있습니다.  
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 10 56 19" src="https://user-images.githubusercontent.com/23609119/166939010-6592c0bc-112a-4c6e-b951-8003fb529387.png"></p>

프로세스를 좌클릭하여 수정할 수 있게 만들 수 있습니다. 좌클릭한 상태에서는 좌클릭한 프로세스의 Process Name, Arrival Time, Workload를 수정할 수 있으며 우클릭을 하여 Context Menu를 열 수 있습니다.
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 10 59 02" src="https://user-images.githubusercontent.com/23609119/166939550-3821bbac-1e03-44de-8937-dcceb0dcb84f.png"></p>

프로세스의 Arrival Time은 간트 차트의 Arrival Time에 자동으로 반영됩니다. 
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 07 51" src="https://user-images.githubusercontent.com/23609119/166941358-1f5907ba-362f-4dc7-8712-097277a19b58.png"></p>

프로세스의 수는 제한이 없습니다. 다만 너무 많은 프로세스를 적용 시 동작이 느려질 수 있습니다(M1 MacBook Air에서 1500개 프로세스까지 테스트).
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 14 08" src="https://user-images.githubusercontent.com/23609119/166942763-e25881fd-467d-4c4e-8f67-e28be3e6e84d.png"></p>

### Processor
프로세서를 추가/삭제하거나 코어의 켜짐/꺼짐, P-Core/E-Core 상태를 변경할 수 있습니다.  
스케줄링 알고리즘이 작동되는 중에는 각 코어 영역 하단부에 전력 소모 및 전체 시간 대비 일을 하고 있는(프로세스가 점유된) 시간, 전체 전력 소모, 평균 이용률이 interval마다 반영됩니다. 
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 01 41" src="https://user-images.githubusercontent.com/23609119/166940092-68353c2a-11c9-40ea-86be-42cf64e93b1c.png"></p> 

알고리즘이 작동 중일때 코어 별로 현재 점유하고 있는 프로세스를 확인할 수 있습니다.
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 25 31" src="https://user-images.githubusercontent.com/23609119/166945133-25709638-06af-4134-b732-5714f3c68086.png"></p>

프로세서의 코어 수는 제한이 없습니다. 다만 너무 많은 코어 수를 적용 시 동작이 느려질 수 있으며 화면 크기에 따라 코어 조작 및 정보 확인이 어려울 수 있습니다(M1 MacBook Air에서 64개 코어까지 테스트). 
<p align="center"><img width="75%" alt="스크린샷 2022-05-05 오후 11 17 04" src="https://user-images.githubusercontent.com/23609119/166943369-f2bdb61a-ea46-4f02-950a-f2dcef66983b.png"></p>

### Runner Tool
Scheduling Algorithm과 RR Quantum(if available), interval을 지정할 수 있습니다(Known issues 1).
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 29 19" src="https://user-images.githubusercontent.com/23609119/166945925-5602e828-2cfb-4930-9b62-29a9a618afc1.png"></p>

RUN!! 버튼을 누르면 Scheduling Algorithm이 실행되며 다음과 같이 일시정지와 정지를 할 수 있습니다.
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 37 19" src="https://user-images.githubusercontent.com/23609119/166947695-5ca9e008-67fc-44fd-83c4-9ff52bfb14d7.png"></p>



### Ready Queue and Gantt Chart
알고리즘 작동 중 Ready Queue의 상태 및 Gantt Chart를 확인할 수 있습니다.
<p align="center"><img width="50%" alt="May-05-2022 23-44-08" src="https://user-images.githubusercontent.com/23609119/166949255-bef3c3a1-24ac-45e7-9ace-1674ee958d5f.gif"></p>  

1. Gantt Chart에 현재 시간 위치에 세로 선이 표시되어 현재 위치가 어디인지 파악할 수 있습니다.  
2. 알고리즘이 작동 중일때는 매 interval마다 현재 시간 위치로 자동 스크롤됩니다.  
3. Arrival Time에는 각 프로세스의 Arrival Time이 깃발 형식으로 표현되어 있습니다.  

Gantt Chart의 Time Scale Bar에 마우스 커서를 두고 Shift + Mouse Scroll로 특정 시간 위치로 스크롤하며 이동할 수 있습니다.
Gantt Chart의 +, - 버튼으로 축적(accumulation)을 변경할 수 있습니다.
<p align="center"><img width="50%" alt="May-06-2022 00-03-14" src="https://user-images.githubusercontent.com/23609119/166953480-6e891de6-ea20-4ce8-9787-a7f0ca14a57f.gif"></p>  

### Result Table
Result Table에서 작업이 완료된 프로세스의 목록을 확인할 수 있습니다.
<p align="center"><img width="50%" alt="스크린샷 2022-05-06 오전 12 06 18" src="https://user-images.githubusercontent.com/23609119/166953834-2e5354bc-a707-4469-85a4-a090699f8ccc.png"></p>

#### Termiologies

1. AT: Arrival Time
2. BT: Burst Time 프로세스의 일을 처리하는 데 걸리는 시간
3. WT: Waiting Time = TT - BT
4. TT: Turnaround Time
5. NTT: Normalized Turnaround Time = TT / BT

## Known Issues
1. Interval text field에 포커스를 맞추고 Enter 키를 누르면 interval이 100으로 설정되는 문제
2. Scheduling Algorithm이 작동 중 일시정지 시 프로세스, 프로세서 정보 등을 수정할 수 있는 문제
3. Gantt Chart의 축적이 작을 경우 Scale bar의 숫자가 잘리는 문제
4. Random Process Generator의 Range Scale Bar의 start와 end를 같게 지정한 후 Rande Scale Bar를 다시 조작하면 Error 발생 후 Random Process Generator window가 종료되는 문제
5. 동일한 process, processor, algorithm 상태에서 interval을 0으로 맞추고 run 버튼을 여러 번 누르면 두 번째 run부터 GUI가 갱신되지 않음
