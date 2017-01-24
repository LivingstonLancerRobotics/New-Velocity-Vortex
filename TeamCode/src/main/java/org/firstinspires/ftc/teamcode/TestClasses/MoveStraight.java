package org.firstinspires.ftc.teamcode.TestClasses;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.DriversAndHardware.Hardware3415;

/**
 * Created by shlok.khandelwal on 1/23/2017.
 */
@Autonomous(name="Encoder Drive", group="Tests")
public class MoveStraight extends LinearOpMode {
    Hardware3415 balin = new Hardware3415();
    public void runOpMode(){
        balin.init(hardwareMap, true);
        balin.navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(Hardware3415.cdim),
                Hardware3415.NAVX_DIM_I2C_PORT,
                AHRS.DeviceDataType.kProcessedData,
                Hardware3415.NAVX_DEVICE_UPDATE_RATE_HZ);
        //Prevents Balin from running before callibration is complete
        while (balin.navx_device.isCalibrating()) {
            telemetry.addData("Ready?", "No");
            telemetry.update();
        }
        telemetry.addData("Ready?", "Yes");
        telemetry.update();
        balin.navx_device.zeroYaw();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        balin.moveStraight(12, true, this);
        balin.restAndSleep(this);
        sleep(2000);
        balin.moveStraight(12, false, this);
        balin.restAndSleep(this);
    }
}
