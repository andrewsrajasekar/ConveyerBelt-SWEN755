# Conveyer Belt

### Scenario
Consider a system that runs on conveyor belts, like a sushi restaurant.
There is only a specific number of products allowed on the conveyor belt (threshold).
If there are not enough products, or too many products, the conveyor belt is faulty and will need to be fixed.

### Implementation of Heartbeat Algorithm for Fault Detection
The conveyor belts are equipped with light sensors that count the number of products that pass by.
Each conveyor belt is designed to handle a specific rate of products per minute or per second (e.g., 100 products/minute, 2 products/second). 
If the rate is greater or lesser than the specific rate (due to change in belt speed, or change in number of products on the belt, etc), the conveyor belt will stop working and stop sending heartbeat signals to the receiver.  This "crash" is a safeguard to prevent physical damage or further malfunctions.

### Implementation of Passive Redundancy for Fault Recovery
The system is designed to have a backup conveyor belt that can be used in case of a fault.
The fault monitor notifies the state manager of the fault, and the state manager will switch to the backup conveyor belt.
To ensure that the backup conveyor belt is synchronized, we use checkpoints which store the state of the primary belt every set interval.

### Team members
- Andrews Rajasekar
- Sam Singh Anantha
- Vidit Naithani

### Instructions
1. Clone the github repository
2. Run the main function on heartbeat receiver
3. Run the main function on State Manager
