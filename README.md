# Ragdoll

## Introduction
***Ragdoll*** is a Java-made animated doll. You may drag its head, arms, legs, 
feet to rotate them (with in a limited range). Also, the legs are resizable. You
may click on a leg, drag and drop to resize them.

## Instruction
### Menu Bar
- **Reset (Ctrl + R)**: Click on this to reset the doll to its original position
and pose.
- **Save (Ctrl + S)**: Save the current position and pose to a JSON file.
- **Load (Ctrl + L)**: Load from a previously saved JSON file. It will set the 
ragdoll to the state saved in the selected file.
- **Quit (Ctrl + Q)**: Exit this program.

### Animation Master
This is a Flash-like animation interface that enables you to make a 5-second 
animation.

To make an animation, follow these steps:
1. Drag the slider to the desired time position.
2. Play with the ragdoll, set it to your desired pose / position.
3. Click on **Keyframe** to set the current state as a keyframe.
4. Goto 1 for another keyframe.

Note that: 
- if you want to modify a keyframe, just drag the slider to that position,
change the pose, hit **Keyframe** again to rewrite it.
- Keyframes are labelled with **|** under the slider. Keyframes that lie on a exact
second position are labelled with **(0s)** as an example. 
- When you finish, drag the slider to any position and hit **Play** to start
playing the animation. You will see the doll transitioning from one keyframe
to another smoothly.
- You may pause at any time.
- If the animation stops (reaches 5s), simply hit **Play** again to replay it.

### Build Notes
- This program is developed and tested under macOS Mojave 10.14.2 Developer Beta 2.
With Java SE 10.0.2.

Enjoy playing with it :)