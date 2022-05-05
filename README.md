# os-process-scheduling-simulator
2022-1학기 KOREATECH 운영체제 프로세스 스케쥴링 알고리즘 과제  
Instructor: Duksu Kim

<p align="center">
   <img width="75%" alt="스크린샷 2022-05-05 오후 10 37 42" src="https://user-images.githubusercontent.com/23609119/166935181-2ea9d4f1-f524-4cd8-b77c-e09cc9c020f6.png">
   <img width="75%" alt="스크린샷 2022-05-05 오후 10 39 25" src="https://user-images.githubusercontent.com/23609119/166935502-9b5914c6-94a4-4698-a336-91d0890ba1b8.png">

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
작동원리 작성 "해줘"

## Usage
### Process
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 10 56 19" src="https://user-images.githubusercontent.com/23609119/166939010-6592c0bc-112a-4c6e-b951-8003fb529387.png"></p>
원하는 프로세스명, Arrival Time, Workload를 입력 후 Add 버튼을 눌러 프로세스를 추가할 수 있습니다.  
Export to.. 기능으로 현재 프로세스의 상태를 json 파일로 내보낼 수 있으면 Import from.. 기능으로 json 파일로부터 프로세스의 상태를 불러올 수 있습니다.  
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 10 59 02" src="https://user-images.githubusercontent.com/23609119/166939550-3821bbac-1e03-44de-8937-dcceb0dcb84f.png"></p>
프로세스를 좌클릭하여 수정할 수 있게 만들 수 있습니다. 좌클릭한 상태에서는 좌클릭한 프로세스의 Process Name, Arrival Time, Workload를 수정할 수 있으며 우클릭을 하여 Context Menu를 열 수 있습니다.

### Processor
<p align="center"><img width="50%" alt="스크린샷 2022-05-05 오후 11 01 41" src="https://user-images.githubusercontent.com/23609119/166940092-68353c2a-11c9-40ea-86be-42cf64e93b1c.png"></p>
프로세서를 
