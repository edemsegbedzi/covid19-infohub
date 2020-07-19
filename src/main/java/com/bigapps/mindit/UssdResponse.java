package com.bigapps.mindit;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * This class serves as a facade for the Rancard's ussd Response
 * The class is design to meet the required parameters for delivering a ussd response to Rancard's smpp.
 * The variable of the class are converted into JSON by the spring framework during runtime
 *
 * Created by edem on 1/31/17.
 */


@Component
@Data
public class UssdResponse {
    private String message;
    private boolean continueSession = true;

}