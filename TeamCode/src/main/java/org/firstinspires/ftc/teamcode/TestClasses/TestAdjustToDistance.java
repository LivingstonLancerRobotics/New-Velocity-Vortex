package org.firstinspires.ftc.teamcode.TestClasses;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.DriversAndHardware.Hardware3415;

/**
 * Created by andrew.keenan on 2/22/2017.
 */

@Autonomous(name="Adjust To Distance Test", group="Test")
public class TestAdjustToDistance extends LinearOpMode {
    Hardware3415 balin = new Hardware3415();
    public void runOpMode() {
        balin.init(hardwareMap, true);
        waitForStart();
        adjustToDistance(12.0, .3);
    }

    public void adjustToDistance(double distance, double power) {
        double currDistance = readSonar();
        while (opModeIsActive() && !isStopRequested() && currDistance > distance + 2 && currDistance < distance - 2) {
            if(currDistance < distance) {
                balin.setDrivePower(-power);
                while(currDistance < distance && opModeIsActive() && !isStopRequested()) {
                    currDistance = readSonar();
                }
            }
            else if (currDistance > distance) {
                balin.setDrivePower(power);
                while(currDistance > distance && opModeIsActive() && !isStopRequested()) {
                    currDistance = readSonar();
                }
            }
            currDistance = readSonar();
        }
        balin.restAndSleep(this);
    }

    public double readSonar() {
        double sonarData = balin.sonar.cmUltrasonic();
        return sonarData;
    }
}