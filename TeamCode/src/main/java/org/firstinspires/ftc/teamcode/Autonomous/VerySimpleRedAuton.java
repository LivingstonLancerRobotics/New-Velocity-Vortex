package org.firstinspires.ftc.teamcode.Autonomous;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.DriversAndHardware.Hardware3415;

/**
 * Created by andrew.keenan on 2/26/2017.
 */
@Autonomous(name = "asdf", group = "tests")
public class VerySimpleRedAuton extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    Hardware3415 balin = new Hardware3415();

    public void runOpMode() {
        balin.init(hardwareMap, true);
        balin.navx_device = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(balin.cdim),
                balin.NAVX_DIM_I2C_PORT,
                AHRS.DeviceDataType.kProcessedData,
                balin.NAVX_DEVICE_UPDATE_RATE_HZ);
        while (balin.navx_device.isCalibrating() && !isStopRequested()) {
            telemetry.addData("Ready?", "No");
            telemetry.update();
        }
        telemetry.addData("Ready?", "Yes");
        telemetry.update();
        balin.changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        sleep(20000);
        //balin.turn();
        balin.navx_device.zeroYaw();
        balin.moveStraightnew(14, this);
        balin.setDrivePower(0);
        balin.collector.setPower(0);
        sleep(500);

        //Shoot first particle
        balin.shoot(1.0);
        sleep(600);
        balin.shoot(0);


        sleep(200);
        balin.setDrivePower(.4);
        sleep(1800);
        balin.setDrivePower(0);

    }
}
