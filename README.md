# APAirport-Simulation

This is an airport simulation program that simulates the operations of a small airport called Asia Pacific Airport. The program focuses on synchronization and communication between concurrent processes to manage the landing and departing of planes.

## Basic Requirements

The following are the basic requirements of the airport simulation:

- There is only one runway available for all planes to land and depart.
- The airport can accommodate a maximum of three airplanes on the airport grounds, including the runway, to prevent collisions.
- Upon obtaining permission to land, an aircraft will follow a sequence of steps: landing on the runway, coasting to the assigned gate, docking to the gate, allowing passengers to disembark, refilling supplies and fuel, receiving new passengers, undocking from the gate, coasting to the assigned runway, and finally taking off.
- Each step in the process takes some time, simulating realistic airport operations.
- Due to limited space, there is no waiting area on the ground for planes to wait for an available gate.

## Additional Requirements

In addition to the basic requirements, the airport simulation program includes the following additional features:

- **Passenger Disembarking/Embarking**: The simulation includes concurrent processes for passengers disembarking from and embarking onto the planes at the three available gates. This allows realistic handling of passengers during the airport operations.

- **Refill Supplies and Cleaning of Aircraft**: The simulation incorporates a process for refilling supplies and cleaning the aircraft. This process happens concurrently with other operations to ensure efficient handling of the planes.

- **Exclusive Refueling**: Since there is only one refueling truck available, the refueling of aircraft is an exclusive event. This means that while other processes can happen concurrently, only one aircraft can be refueled at a time.

- **Congested Scenario**: The simulation includes a congested scenario where two planes are waiting to land while the two gates are occupied. In this scenario, a third plane with fuel shortage requires an emergency landing. This adds complexity to the simulation and allows for handling critical situations.

## Statistics

At the end of the simulation, when all planes have left the airport, the ATC manager performs sanity checks and prints out statistics on the run. The following statistics are collected:

- **Empty Gates Check**: The ATC manager checks that all gates are indeed empty, ensuring that there are no planes or passengers remaining at the gates after the simulation.

- **Waiting Time Statistics**: The simulation collects statistics on the maximum, average, and minimum waiting time for a plane. This provides insights into the efficiency of airport operations and helps identify areas for improvement.

- **Number of Planes Served/Passengers Boarded**: The ATC manager prints out the total number of planes served during the simulation, indicating the successful landing and departing of planes. Additionally, the number of passengers boarded onto the planes is also reported.

## Acknowledgments

The airport simulation program is developed as part of an assignment.
