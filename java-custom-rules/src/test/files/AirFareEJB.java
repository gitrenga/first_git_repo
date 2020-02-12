
/* ====================================== */
/* Copyright (c) 2006 Unisys Corporation. */
/*          All rights reserved.          */
/*          UNISYS CONFIDENTIAL           */
/* ====================================== */

package com.unisys.trans.aircore.far.airfareejb;

//imports from aircore package.
import com.unisys.trans.aircore.far.constant.FareLogIdConstants;
import com.unisys.trans.aircore.far.constant.FareReasonCodeConstants;
import com.unisys.trans.aircore.far.gateway.SharedGateway;
import com.unisys.trans.aircore.far.param.AirfarePriceRequestParam;
import com.unisys.trans.aircore.far.param.AirfarePriceResponseData;
import com.unisys.trans.aircore.far.param.FareCategoryInformationParam;
import com.unisys.trans.aircore.far.param.FareCategoryParam;
import com.unisys.trans.aircore.far.param.FareCategoryRequestParam;
import com.unisys.trans.aircore.far.param.FareCategoryResponseParam;
import com.unisys.trans.aircore.far.param.FareInformationParam;
import com.unisys.trans.aircore.far.param.FareRepricerResponseParam;
import com.unisys.trans.aircore.far.param.FareRequestParam;
import com.unisys.trans.aircore.far.param.FareResponseParam;
import com.unisys.trans.aircore.far.param.FareRuleParagraphParam;
import com.unisys.trans.aircore.far.param.FareRuleParagraphRequestParam;
import com.unisys.trans.aircore.far.param.FareRuleParagraphResponseParam;
import com.unisys.trans.aircore.far.param.FareRuleParam;
import com.unisys.trans.aircore.far.param.FaresSearchResponseParam;
import com.unisys.trans.aircore.far.param.InformativePriceRequestDataParam;
import com.unisys.trans.aircore.far.param.InformativePriceRequestParam;
import com.unisys.trans.aircore.far.param.InformativePriceResponseData;
import com.unisys.trans.aircore.far.param.InformativePriceResponseParam;
import com.unisys.trans.aircore.far.param.ItineraryParam;
import com.unisys.trans.aircore.far.param.OtherFareDetailsRequestParam;
import com.unisys.trans.aircore.far.param.OtherFareDetailsResponseParam;
import com.unisys.trans.aircore.far.param.SitaFlightSegmentParam;
import com.unisys.trans.aircore.far.param.SitaSupportFareAndPricingParam;
import com.unisys.trans.aircore.far.param.SupportFareAndPricingParam;
import com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse;
import com.unisys.trans.aircore.far.service.FareService;
import com.unisys.trans.aircore.far.utils.ExceptionCreator;
import com.unisys.trans.aircore.far.utils.FareValidator;
import com.unisys.trans.aircore.far.validator.constant.FareValidationLogIdConstants;
import com.unisys.trans.aircore.far.validator.constant.FareValidationReasonCodeConstants;
//Imports from Shared package
import com.unisys.trans.shared.util.constant.SharedConstants;
import com.unisys.trans.shared.util.ejb3.AbstractSessionBean;
import com.unisys.trans.shared.util.logging.SharedLogger;
import com.unisys.trans.shared.util.response.ResponseData;
import com.unisys.trans.shared.util.response.SharedException;
import com.unisys.trans.shared.util.string.StringUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.annotation.security.DeclareRoles;

import com.unisys.trans.aircore.far.airfareejb.AirFare;


/**
 * This is the stateless session bean. This class communicates with FareService
 * for get the fare operations and gives back the data to the presentation layer.
 *
 * @author Unisys
 */
@DeclareRoles(value = { "Everyone", "SUPER_ROLE", "AIRCORE_INTERNAL_ROLE",
		"RESERVATION_FARES_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
@RunAs(value = "AIRCORE_INTERNAL_ROLE")
@Stateless(name="AirFareEJB",mappedName="AirFareEJB")
@Remote(com.unisys.trans.aircore.far.airfareejb.AirFare.class)
public class AirFareEJB extends AbstractSessionBean {
    /**
     * Default constructor.
     */

    public AirFareEJB() {

    }
   /**
    * This method gets detail information about Fare Rule Paragraphs requested
    * by the user<br>
    * <p>
    * The method does the following actions in sequence
    * <ol>
    *
    * <li>
    * Does the Security check and ensures that the agent invoking this method
    * has access rights to do so.
    *
    * <li>
    * Invokes the {@link FareService#getFareRuleParagraphs} to retrieve the
    * fares rules for the requested type of fare.
    *
    * </ol>
    * </p>
    *
    * @param  pFareRuleParagraphRequest   Holds the name of attributes that is
    *                                     requested. The fares for which the
    *                                     fare rule has to be obtained.
    *
    * @return FareRuleParagraphResponse   FareRuleParagraphResponse the value
    *                                     of requested property having the rules
    *                                     for the resquested fares.

    * @throws SharedException             if any condition was encountered that
    *                                     prevented the rollback of this EJB
    *                                     transaction.
    */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
   public FareRuleParagraphResponseParam getFareRuleParagraphs(final FareRuleParagraphRequestParam
               pFareRuleParagraphRequest)throws SharedException {

        // Begin collection of EJB statistics
         final Object[] methodArgs = {pFareRuleParagraphRequest};
        

         //Gets the list of FareRuleParam to create FareRuleParagraphResponse
         final FareRuleParagraphResponseParam aFareRuleParagraphResponse = new FareRuleParagraphResponseParam();

         try {
             super.onMethodEntryValidate("getFareRuleParagraphs(FareRuleParagraphRequest)", methodArgs);
			 //Context validation
             ResponseData aResponseData = this.validateForGetFareRuleParagraphs(pFareRuleParagraphRequest);
             if (!aResponseData.isStatusSuccessful()) {
                 aFareRuleParagraphResponse.setAllFromAResponseData(aResponseData);
             }
             else {
             final List aFareRuleParamList = new FareService()
                        .getFareRuleParagraphs(pFareRuleParagraphRequest);
             aFareRuleParagraphResponse.setStatusSuccessful();
             aFareRuleParagraphResponse.setFareRuleParamList(aFareRuleParamList);
			}
           }

         catch (SharedException aSharedException) {
             aFareRuleParagraphResponse.setAllFromAResponseData(aSharedException.getResponseData());
           }

         catch (Throwable aThrowable) {
             aFareRuleParagraphResponse.setStatusFatalReadOnly();
             aFareRuleParagraphResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                               SharedConstants.FARE_APPLICATION);
             aFareRuleParagraphResponse.setDebugMessage(
                "AirFareEJB:getFareRuleParagraphs(FareRuleParagraphRequest):Unexpected Exception");
             // Logs the exception
             SharedLogger.log(FareLogIdConstants.LOG_ID_1035, aThrowable, aFareRuleParagraphResponse);
           }
         finally {
              // EJBExit calls once the EJB processing is over.
              super.onMethodExit(aFareRuleParagraphResponse);
              releaseResource();
           }

         return aFareRuleParagraphResponse;

    }

    /**
     * This method gets information about Fares for the requested fare type and
     * fare details by the user<br>
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     *
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getFares} to retrieve the fares for the
     * requested type of fare.
     *
     * </ol>
     * </p>
     *
     * @param  pFareRequest     Holds the name of property that is requested.
     *                          The origin the destination and the journey
     *                          details.
     * @return                  FareResponse the value of requested property
     * @throws SharedException  if any condition was encountered that prevented
     *                          the rollback of this EJB transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public FareResponseParam getFares(final FareRequestParam pFareRequest)throws SharedException {


        // Begin collection of EJB statistics
        final Object[] methodArgs = {pFareRequest};
       
        final FareResponseParam aFareResponse = new FareResponseParam();

        try {
            super.onMethodEntryValidate("getFares(FareRequest)", methodArgs);
            ResponseData aResponseData = this.validateForGetFares(pFareRequest);
            if (!aResponseData.isStatusSuccessful()) {
                aFareResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            // Calls the service layer to get all the Fares depending on the
            // request.
            final List aFareInformationParamList = new FareService().getFares(pFareRequest);
            aFareResponse.setFareInformationParamList(aFareInformationParamList);
            aFareResponse.setStatusSuccessful();
          }
        }
        catch (SharedException aSharedException) {
        aFareResponse.setAllFromAResponseData(aSharedException.getResponseData());
          }

        catch (Throwable aThrowable) {
            aFareResponse.setStatusFatalReadOnly();
            aFareResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                              SharedConstants.FARE_APPLICATION);
            aFareResponse.setDebugMessage(
               "AirFareEJB:getFares(FareRequest):Unexpected Exception");
            // Logs the exception
            SharedLogger.log(FareLogIdConstants.LOG_ID_1034, aThrowable, aFareResponse);
          }
        finally {
             // EJBExit calls once the EJB processing is over.
             super.onMethodExit(aFareResponse);
          }

        return aFareResponse;

    }


   /**
    * This method gets detail information about Fare Categories requested
    * by the user<br>
    *
    * <p>
    * The method does the following actions in sequence
    * <ol>
    *
    * <li>
    * Does the Security check and ensures that the agent invoking this method
    * has access rights to do so.
    *
    * <li>
    * Invokes the {@link FareService#getFareCategories} to retrive the
    * fare categories ,the category under which the requested fare is specified.
    *
    * </ol>
    * </p>
    *
    * @param  pFareCategoryRequest        Holds the name of property that is
    *                                     requested. The fares for which the
    *                                     fare categories has to be obtained.
    *
    * @return FareCategoryRespose         FareCategoryRespose the the value
    *                                     of requested property having the fare
    *                                     categories for the resquested fares.
    * @throws SharedException             if any condition was encountered that
    *                                     prevented the rollback of this EJB
    *                                     transaction.
    */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
   public FareCategoryResponseParam getFareCategories(final FareCategoryRequestParam pFareCategoryRequest)
            throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pFareCategoryRequest};
      

        final FareCategoryResponseParam aFareCategoryRespose = new FareCategoryResponseParam();
        List aFareCategoryParamList = new ArrayList();

        try {
            super.onMethodEntryValidate("getFareCategories(FareCategoryRequest)", methodArgs);
			//Context validation
            ResponseData aResponseData = this.validateForGetFareCategories(pFareCategoryRequest);
            if (!aResponseData.isStatusSuccessful()) {
                aFareCategoryRespose.setAllFromAResponseData(aResponseData);
            }
            else {
            //get the list of FareCategoryParam to create FareCategoryRespose
            aFareCategoryParamList = new FareService().getFareCategories(pFareCategoryRequest);
            aFareCategoryRespose.setStatusSuccessful();
            aFareCategoryRespose.setFareCategoryParamList(aFareCategoryParamList);
			}
        }

        catch (SharedException aSharedException) {
            aFareCategoryRespose.setAllFromAResponseData(aSharedException.getResponseData());
        }

        catch (Throwable aThrowable) {
            aFareCategoryRespose.setStatusFatalWrite();
            aFareCategoryRespose.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                        SharedConstants.FARE_APPLICATION);
            aFareCategoryRespose.setDebugMessage(
                "AirFareEJB: getFareCategories(FareCategoryRequest):Unexpected Exception");
            // Logs the exception
            SharedLogger.log(FareLogIdConstants.LOG_ID_1036, aThrowable, aFareCategoryRespose);
        }
        finally {
            // EJBExit calls once the EJB processing is over.
            super.onMethodExit(aFareCategoryRespose);
        }

        return aFareCategoryRespose;

    }

    /**
     * This method gets detail information about Fare Routings for
     * requested fare basis code by the agent
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     *
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getFareRoutings(OtherFareDetailsRequestParam)}
     * to retrieve the fares routings for the requested fare basis code.
     * It gets the all possible routings for that first/last ticketing date
     * and for one particular fare basis code.Routings are obtained as a list that
     * shows which airports may be transited and the airline designator(s) of the
     * airline(s) that may be used for travel between each airport/city pair.
     *
     * </ol>
     * </p>
     *
     * @param  pOtherFareDetailsRequest    Holds the name of property that is
     *                                     requested.fare basis code and ticketing
     *                                     date.
     *
     * @return OtherFareDetailsResponse    OtherFareDetailsResponse the value
     *                                     of requested property having the routings
     *                                     for the resquested fare basis code.
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
	public OtherFareDetailsResponseParam getFareRoutings(
                final OtherFareDetailsRequestParam pOtherFareDetailsRequest) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pOtherFareDetailsRequest};
        super.onMethodEntry("getFareRoutings(OtherFareDetailsRequest)", methodArgs);

        OtherFareDetailsResponseParam anOtherFareDetailsResponse = new OtherFareDetailsResponseParam();

        try {
        	ResponseData aResponseData = this
				.validateForGetFareRoutings(pOtherFareDetailsRequest);

            if (aResponseData.isStatusSuccessful()) {
            anOtherFareDetailsResponse = new FareService().getFareRoutings(pOtherFareDetailsRequest);
            anOtherFareDetailsResponse.setStatusSuccessful();
            }
            else {
                anOtherFareDetailsResponse.setStatusFromAResponseData(aResponseData);
            }
          }
        catch (SharedException aSharedException) {
            anOtherFareDetailsResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }

        catch (Throwable aThrowable) {
            anOtherFareDetailsResponse.setStatusFatalWrite();
            anOtherFareDetailsResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                        SharedConstants.FARE_APPLICATION);
            anOtherFareDetailsResponse.setDebugMessage(
                "AirFareEJB: getFareRoutings(OtherFareDetailsRequest):Unexpected Exception");
            // Logs the exception
            SharedLogger.log(FareLogIdConstants.LOG_ID_1077, aThrowable, anOtherFareDetailsResponse);
        }
        finally {
            // EJBExit calls once the EJB processing is over.
            super.onMethodExit(anOtherFareDetailsResponse);
        }

        return anOtherFareDetailsResponse;
    }

    /**
     * This method gets detail information about Fare Reservation
     * conditions for requested fare basis code by the agent
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     *
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getFareReservation(OtherFareDetailsRequestParam)}
     * to retrieve the fares Reservations for the requested fare basis code.
     * It gets the reservation conditions which consist of:
     * Prime inventory class (booking code)
     * Inventory class exceptions listed by airline designator
     *
     * </ol>
     * </p>
     *
     * @param  pOtherFareDetailsRequest    Holds the name of property that is
     *                                     requested.fare basis code and ticketing
     *                                     date.
     *
     * @return OtherFareDetailsResponse    OtherFareDetailsResponse the value
     *                                     of requested property having the
     *                                     Reservation conditions information
     *                                     for the resquested fare basis code.
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
   public OtherFareDetailsResponseParam getFareReservation(
                final OtherFareDetailsRequestParam pOtherFareDetailsRequest) throws SharedException {
       // Begin collection of EJB statistics
       final Object[] methodArgs = {pOtherFareDetailsRequest};
       super.onMethodEntry("getFareReservation(OtherFareDetailsRequest)", methodArgs);

       OtherFareDetailsResponseParam anOtherFareDetailsResponse = new OtherFareDetailsResponseParam();

       try {

            ResponseData aResponseData = this.validateForGetFareReservation(pOtherFareDetailsRequest);
            if (aResponseData.isStatusSuccessful()) {
                anOtherFareDetailsResponse = new FareService().getFareReservation(pOtherFareDetailsRequest);
                anOtherFareDetailsResponse.setStatusSuccessful();
            }
            else {
                anOtherFareDetailsResponse = new OtherFareDetailsResponseParam();
                anOtherFareDetailsResponse.setAllFromAResponseData(aResponseData);
            }
        }
       catch (SharedException aSharedException) {
           anOtherFareDetailsResponse.setStatusFromAResponseData(aSharedException.getResponseData());
       }

       catch (Throwable aThrowable) {
           anOtherFareDetailsResponse.setStatusFatalWrite();
           anOtherFareDetailsResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                       SharedConstants.FARE_APPLICATION);
           anOtherFareDetailsResponse.setDebugMessage(
               "AirFareEJB: getFareReservation(OtherFareDetailsRequest):Unexpected Exception");
           // Logs the exception
           SharedLogger.log(FareLogIdConstants.LOG_ID_1078, aThrowable, anOtherFareDetailsResponse);
       }
       finally {
           // EJBExit calls once the EJB processing is over.
           super.onMethodExit(anOtherFareDetailsResponse);
       }
       return anOtherFareDetailsResponse;
      }

   /**
    * This method gets detail information about Fare Add-Ons
    * conditions for requested fare basis code by the agent
    *
    * <p>
    * The method does the following actions in sequence
    * <ol>
    *
    * <li>
    * Does the Security check and ensures that the agent invoking this method
    * has access rights to do so.
    *
    * <li>
    * Invokes the {@link FareService#getFareAddOns(OtherFareDetailsRequestParam)}
    * to retrieve the fares Add -Ons for the requested fare basis code.
    * It gets the followinf information.
    * Applicable add-on city(s)
    * Add-on amount(s)
    * </ol>
    * </p>
    *
    * @param  pOtherFareDetailsRequest    Holds the name of property that is
    *                                     requested.fare basis code and ticketing
    *                                     date.
    *
    * @return OtherFareDetailsResponse    OtherFareDetailsResponse the value
    *                                     of requested property having the
    *                                     Fare Add-ons information for the
    *                                     resquested fare basis code.
    * @throws SharedException             if any condition was encountered that
    *                                     prevented the rollback of this EJB
    *                                     transaction.
    */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
   public OtherFareDetailsResponseParam getFareAddOns(final OtherFareDetailsRequestParam pOtherFareDetailsRequest)
        throws SharedException {
        // Begin collection of EJB statistics
        final Object[] methodArgs = {pOtherFareDetailsRequest};
        super.onMethodEntry("getFareAddOns(OtherFareDetailsRequest)", methodArgs);

        OtherFareDetailsResponseParam anOtherFareDetailsResponse = new OtherFareDetailsResponseParam();

        try {

            ResponseData aResponseData = this.validateForGetFareAddOns(pOtherFareDetailsRequest);

            if (aResponseData.isStatusSuccessful()) {
                anOtherFareDetailsResponse = new FareService().getFareAddOns(pOtherFareDetailsRequest);
                anOtherFareDetailsResponse.setStatusSuccessful();
            }
            else {
                anOtherFareDetailsResponse = new OtherFareDetailsResponseParam();
                anOtherFareDetailsResponse.setAllFromAResponseData(aResponseData);
            }
        }
        catch (SharedException aSharedException) {
            anOtherFareDetailsResponse.setStatusFromAResponseData(aSharedException.getResponseData());
        }
        catch (Throwable aThrowable) {
            anOtherFareDetailsResponse.setStatusFatalWrite();
            anOtherFareDetailsResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                                        SharedConstants.FARE_APPLICATION);
            anOtherFareDetailsResponse.setDebugMessage(
                "AirFareEJB: getFareAddOns(OtherFareDetailsRequest):Unexpected Exception");
            // Logs the exception
            SharedLogger.log(FareLogIdConstants.LOG_ID_1079, aThrowable, anOtherFareDetailsResponse);
        }
        finally {
            // EJBExit calls once the EJB processing is over.
            super.onMethodExit(anOtherFareDetailsResponse);
        }
        return anOtherFareDetailsResponse;
      }

    /**
     * Retrieves the excess baggage information.
     * <p>
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getExcessBaggageCharge}
     * to retrieve the excess baggage information that is requested based on the
     * inputs.
     * <li>
     * Then this method sets the <code>ExcessBaggageParamList</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse}
     * </ol>
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.utils.fare.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getExcessBaggageCharge(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};
        
        SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getExcessBaggageCharge(SupportFareAndPricingParam)", methodArgs);
			//Context validation
            ResponseData aResponseData = this.validateForGetExcessBaggageCharge(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
                aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            aSupportFareAndPricingResponse =
                new FareService().getExcessBaggageCharge(pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
            }
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        catch (Throwable aThrowable) {
            aSupportFareAndPricingResponse.setStatusFatalWrite();
            aSupportFareAndPricingResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION,
                    SharedConstants.FARE_APPLICATION);
            aSupportFareAndPricingResponse.setDebugMessage(
                    "AirFareEJB: getExcessBaggageCharge(aSupportFareAndPricingRequest):Unexpected Exception");
            // Logs the exception
            SharedLogger.log(FareLogIdConstants.LOG_ID_1106, aThrowable, aSupportFareAndPricingResponse);
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;
    }

//  Start Code - 2.7 Fares Release
    /**
     * This method is used to retrieve list of Transporting Airlines for a given
     * <code>ticketingAirline</code> from the external fare system.
     * <p>
     * According to the Runtime parameter "APPLICATION_TYPE" the flow is routed
     * to OTA system or the SITA Fare system. The value of
     * <code>APPLICATION_TYPE</code> is used to determine if the fares are rerieved
     * from SITA or from OTA system.
     * </p>
     * <p>
     * The {@linkplain SupportFareAndPricingParam ticketingAirline} is unique
     * for each country.
     * <p>
     * This method accepts a <code>SupportFareAndPricingParam</code> object
     * which contains all details that is used to retrieve list of Transporting
     * Airlines.
     * <p>
     * <p>
     * The method does the following in sequence:<br>
     * 1. It invokes the method validateTransportingAirlinesRequest to verify
     * the XML request. 2. It invokes the method getTransportingAirlines to get
     * the response.
     * </p>
     * <b>Errors:</b><br>
     * An error has occurred if the response attribute setStatusSuccessful is
     * set to false. The response attribute reasonCode contains the error reason
     * code and the response attribute debugMessage contains the error text.
     * <p>
     * Possible reasonCodes that may be returned include: <br>
     * {@linkplain com.unisys.trans.aircore.far.constant.FareReasonCodeConstants#UNEXPECTED_EXCEPTION FareReasonCodeConstants.UNEXPECTED_EXCEPTION}<br>
     *
     * @param pSupportFareAndPricingParam
     * @return SupportFareAndPricingResponse <code>
     *      <ul>
     *          <li>response attributes<br>
     *              See Shared ResponseData
     *      </ul>
     * </code>
     * @throws RemoteException
     * @throws SharedException EJBExit throws SharedException.
     *
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getTransportingAirlines(
            SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {

       final Object[] methodArgs = {pSupportFareAndPricingParam};
       super.onMethodEntry("getTransportingAirlines(SupportFareAndPricingParam)", methodArgs);
       SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

       try {
           this.validateTransportingAirlinesRequest(pSupportFareAndPricingParam);

           aSupportFareAndPricingResponse =
               new FareService().getTransportingAirlines(pSupportFareAndPricingParam);
               aSupportFareAndPricingResponse.setStatusSuccessful();
       }
       catch (SharedException aSharedException) {
           aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
       }
       finally {
           super.onMethodExit(aSupportFareAndPricingResponse);
       }
       return aSupportFareAndPricingResponse;
    }
    /**
     * Retrieves the rate of exchange information.
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getRateOfExchange}
     * to retrieve the rate of exchange information that is requested based on the
     * inputs.
     * </ol>
     *
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getRateOfExchange(final SupportFareAndPricingParam
              pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};       
        SupportFareAndPricingResponse aSupportFareAndPricingResponse
                = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getRateOfExchange(SupportFareAndPricingParam)", methodArgs);
        	ResponseData aResponseData =
        		this.validateForGetRateOfExchange(pSupportFareAndPricingParam);
        	if(!aResponseData.isStatusSuccessful()) {
        		aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
        	}
        	else {
            aSupportFareAndPricingResponse =
                new FareService().getRateOfExchange(pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
            pSupportFareAndPricingParam.setIsRateOfExchangeInd(true);
        }
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;

    }

    /**
     * Retrieves the interline agreement information.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getInterlineAgreements}
     * to retrieve the interline agreement information that is requested based on the
     * inputs.
     * <li>
     * Then this method sets an <code>InterlineAgrParam</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse} object.
     *
     * </ol>
     * </p>
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getInterlineAgreements(
            final SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};       
        SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getInterlineAgreements(SupportFareAndPricingParam)", methodArgs);
 			//Context validation
            ResponseData aResponseData = this.validateForGetInterlineAgreements(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
                aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            aSupportFareAndPricingResponse = new FareService()
                    .getInterlineAgreements(pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
            }
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;

    }

    /**
     * Retrieves the mileage by global indicator information.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li> This method invokes the {@link FareService#getMileageByGlobalInd} to
     * retrieve the mileage by global indicator information that is requested
     * based on the inputs.
     * <li> Then this method sets an <code>MileageParamList</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse}
     * object.
     *
     * </ol>
     * </p>
     *
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.utils.fare.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException if any condition was encountered that prevented
     *             the rollback of this EJB transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getMileageByGlobalInd(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};
        SupportFareAndPricingResponse aSupportFareAndPricingResponse
                = new SupportFareAndPricingResponse();

        try {
			//Context validation
 			ResponseData aResponseData = this.validateForGetMileageByGlobalInd(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
                aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            aSupportFareAndPricingResponse = new FareService().getMileageByGlobalInd(
                    pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
            }
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;
    }

    /**
     * Retrieves the mileage calculation information.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getMileageCalculation}
     * to retrieve the  mileage calculation information that is requested
     * based on the inputs.
     * <li>
     * Then this method sets an <code>MileageParamList</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse} object.
     *
     * </ol>
     * </p>
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     *
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getMileageCalculation(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {
                pSupportFareAndPricingParam};       
        SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getMileageCalculation(SupportFareAndPricingParam)", methodArgs);
        	ResponseData aResponseData = this.validateForMileageCalculation(pSupportFareAndPricingParam);
        	if(aResponseData.isStatusSuccessful()) {
            aSupportFareAndPricingResponse = new FareService().getMileageCalculation(
                    pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
        }
        	else {
        		aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
        	}
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;

    }

    /**
     * Retrieves the mileage surcharges information.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getMileageSurcharge}
     * to retrieve the  mileage surcharges information that is requested
     * based on the inputs.
     * <li>
     * Then this method sets an <code>MileageParamList</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse} object.
     *
     * </ol>
     * </p>
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getMileageSurcharge(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {
                pSupportFareAndPricingParam};
        SupportFareAndPricingResponse aSupportFareAndPricingResponse
                = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getMileageSurcharge(SupportFareAndPricingParam)", methodArgs);
            ResponseData aResponseData = this.validateForGetMileageSurcharge(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
                aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            aSupportFareAndPricingResponse = new FareService().getMileageSurcharge(
                    pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
		}
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;

    }

    /**
     * Retrieves the tax information.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li>
     * This method invokes the {@link FareService#getTaxInformation}
     * to retrieve the tax information that is requested
     * based on the inputs.
     * <li>
     * Then this method sets a <code>TaxInfoParam</code> which is an
     * attribute of
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingResponse} object.
     * </ol>
     * </p>
     * @param  pSupportFareAndPricingParam -
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     *
     * @return SupportFareAndPricingResponse
     *
     * @throws SharedException             if any condition was encountered that
     *                                     prevented the rollback of this EJB
     *                                     transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getTaxInformation(
            final SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {

		ResponseData aResponseData;
        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};
        SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getTaxInformation(SupportFareAndPricingParam)", methodArgs);

			aResponseData = this.validateForGetTaxInformation(pSupportFareAndPricingParam);

         	if (!aResponseData.isStatusSuccessful()) {
         		aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
  			}else{
            aSupportFareAndPricingResponse = new FareService().getTaxInformation(pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
        	}
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;
    }

    /**
     *
     * This method is used to retrieve currency conversion information for a
     * given <code>rateType</code>, <code>rateDate</code>,
     * <code>amount</code>, <code>fromCurrency</code> and
     * <code>toCurrency></code> from the external fare system.
     * <p>
     * According to the Runtime parameter "APPLICATION_TYPE" the flow is routed
     * to OTA system or the SITA Fare system. If the value of
     * <code>APPLICATION_TYPE</code> is "LSY" the fares are rerieved from
     * SITA, else the fares are retrieved from OTA system.
     * </p>
     * This method accepts a <code>SupportFareAndPricingParam</code> object
     * which contains all details that is used to retrieve currency conversion
     * information.
     *
     * <p>
     * The method does the following in sequence:<br>
     * 1. It invokes the method validateCurrencyConversionRequest to verify the
     * XML request. 2. It invokes the method getCurrencyConversionRates to get
     * the response. <b>Errors:</b><br>
     * An error has occurred if the response attribute setStatusSuccessful is
     * set to false. The response attribute reasonCode contains the error reason
     * code and the response attribute debugMessage contains the error text.
     * <p>
     * Possible reasonCodes that may be returned include: <br>
     * {@linkplain com.unisys.trans.aircore.far.constant.FareReasonCodeConstants#UNEXPECTED_EXCEPTION FareReasonCodeConstants.UNEXPECTED_EXCEPTION}<br>
     *
     * @param pSupportFareAndPricingParam
     * @return SupportFareAndPricingResponse <code>
     *      <ul>
     *          <li>response attributes<br>
     *              See Shared ResponseData
     *      </ul>
     * </code>
     * @throws RemoteException
     * @throws SharedException EJBExit throws SharedException.
     *
     */

	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getCurrencyConversion(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};        
        SupportFareAndPricingResponse aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getCurrencyConversion(SupportFareAndPricingParam)", methodArgs);
            ResponseData aResponseData = this.validateForGetCurrencyConversion(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
               aSupportFareAndPricingResponse = new SupportFareAndPricingResponse();
               aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
                aSupportFareAndPricingResponse = new FareService()
                            .getCurrencyConversion(pSupportFareAndPricingParam);

                aSupportFareAndPricingResponse.setStatusSuccessful();
                pSupportFareAndPricingParam.setIsCurrencyConversionInd(true);
            }
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
    }

        return aSupportFareAndPricingResponse;
    }


    /**
     * This method is used to retrieve Passenger Facility Charge Information
     * from the external fare system.
     * <p>
     * According to the Runtime parameter "APPLICATION_TYPE" the flow is routed
     * to OTA system or the SITA Fare system. If the value of
     * <code>APPLICATION_TYPE</code> is "LSY" the fares are rerieved from
     * SITA, else the fares are retrieved from OTA system.
     *
     * <p>
     * The
     * {@linkplain com.unisys.trans.aircore.utils.fare.SupportFareAndPricingParam airportCode}
     * is unique for each city.
     * <p>
     * This method accepts a
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam} object
     * which contains all details that is used to retrieve Passenger Facility
     * Charge Information.
     * <p>
     * <b>Errors:</b><br>
     * An error has occurred if the response attribute setStatusSuccessful is
     * set to false. The response attribute reasonCode contains the error reason
     * code and the response attribute debugMessage contains the error text.
     * <p>
     * Possible reasonCodes that may be returned include: <br>
     * {@linkplain com.unisys.trans.aircore.far.constant.FareReasonCodeConstants#REMOTE_EXCEPTION FareReasonCodeConstants.REMOTE_EXCEPTION}<br>
     * {@linkplain com.unisys.trans.aircore.far.constant.FareReasonCodeConstants#UNEXPECTED_EXCEPTION FareReasonCodeConstants.UNEXPECTED_EXCEPTION}<br>
     * {@linkplain com.unisys.trans.shared.util.constant.UtilReasonCodeConstants#REMOTE_EXCEPTION UtilReasonCodeConstants.REMOTE_EXCEPTION}<br>
     * {@linkplain com.unisys.trans.shared.util.constant.UtilReasonCodeConstants#EJB_CREATE_EXCEPTION UtilReasonCodeConstants.EJB_CREATE_EXCEPTION}<br>
     * {@linkplain com.unisys.trans.shared.security.constant.SecurityReasonCodeConstants#ACCESS_DENIED SecurityReasonCodeConstants.ACCESS_DENIED}<br>
     * {@linkplain com.unisys.trans.shared.security.constant.SecurityReasonCodeConstants#SECURITY_INFORMATION_NULL SecurityReasonCodeConstants.SECURITY_INFORMATION_NULL}<br>
     *
     * @param pSupportFareAndPricingParam <code>
     *      <ul>
     *      <li>Security Attributes - (M)<br>
     *          See Shared SharedSecurityParam
     *      <li>airportCode - (M)
     *      </ul>
     * </code>
     * @return SupportFareAndPricingResponse <code>
     *      <ul>
     *          <li>response attributes<br>
     *              See Shared ResponseData
     *      </ul>
     * </code>
     * @throws RemoteException
     * @throws SharedException EJBExit throws SharedException.
     *
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public SupportFareAndPricingResponse getPassengerFacilityCharge(final SupportFareAndPricingParam
            pSupportFareAndPricingParam) throws SharedException {

        // Begin collection of EJB statistics
        final Object[] methodArgs = {pSupportFareAndPricingParam};      
        SupportFareAndPricingResponse aSupportFareAndPricingResponse
                = new SupportFareAndPricingResponse();

        try {
            super.onMethodEntryValidate("getPassengerFacilityCharge(SupportFareAndPricingParam)", methodArgs);
            ResponseData aResponseData = this
                        .validateForGetPassengerFacilityCharge(pSupportFareAndPricingParam);
            if (!aResponseData.isStatusSuccessful()) {
                aSupportFareAndPricingResponse.setAllFromAResponseData(aResponseData);
            }
            else {
            aSupportFareAndPricingResponse = new FareService().getPassengerFacilityCharge(
                    pSupportFareAndPricingParam);
            aSupportFareAndPricingResponse.setStatusSuccessful();
		}
        }
        catch (SharedException aSharedException) {
            aSupportFareAndPricingResponse.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            super.onMethodExit(aSupportFareAndPricingResponse);
        }
        return aSupportFareAndPricingResponse;

    }
//End Code - 2.7 Fares Release

    /**
     * Retrieves the value of the specific property from the
     * <code>Aircore.properties</code> file.
     * <p>
     * The method does the following actions in sequence
     * <ol>
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getInformativePriceList} to retrive the
     * Informative Price List.
     *
     * </ol>
     * <p>
     * @param pInformativePriceRequestParam - link
     * @return InformativePriceResponseData             InformativePriceResponseData the value
     *                                     		of requested property having
     *                                          the Informative Price List.
     *
     *
     * @throws SharedException 		                if any condition was encountered that
     *                               		        prevented the rollback of this EJB
     *                                     		transaction.
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public InformativePriceResponseData getInformativePriceList(
                final InformativePriceRequestParam pInformativePriceRequestParam) throws SharedException {
		final Object[] methodArgs = {pInformativePriceRequestParam};

    	InformativePriceResponseParam aInformativePriceResponseParam = new InformativePriceResponseParam();
    	final InformativePriceResponseData aInformativePriceResponseData = new InformativePriceResponseData();
    	try {
            super.onMethodEntryValidate("getInformativePriceList(InformativePriceRequestParam)",methodArgs);
    		ResponseData bResponseData =
    			this.validateForGetInformativePriceList(pInformativePriceRequestParam);
    		if(!bResponseData.isStatusSuccessful()) {
    			aInformativePriceResponseData.setAllFromAResponseData(bResponseData);
    		}
    		else {
	    	aInformativePriceResponseParam = new FareService()
                        .getInformativePriceList(pInformativePriceRequestParam);
	    	aInformativePriceResponseData.setInformativePriceResponseParam(aInformativePriceResponseParam);
	    	aInformativePriceResponseData.setStatusSuccessful();
    		}
    	}
    	catch (SharedException aSharedException) {
    		aInformativePriceResponseData.setStatusFatalWrite();
    		aInformativePriceResponseData.setReasonCode(FareReasonCodeConstants.CASTOR_MARSHAL_EXCEPTION,
	                                          SharedConstants.FARE_APPLICATION);
    		aInformativePriceResponseData.setDebugMessage(
	           "AirFareEJB:getInformativePriceList(FareRequest):Shared Exception");
    		}
    	finally {
    		// EJBExit calls once the EJB processing is over.
    		super.onMethodExit(aInformativePriceResponseData);
    		}
    	return aInformativePriceResponseData;
    }
    /**
     * Retrieves the value of the specific property from the
     * <code>Aircore.properties</code> file.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     *
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getFareQuote} to retrive the
     * lowest fare quote.
     *
     * </ol>
     * <p>
     * @param  pAirfarePriceRequestParam		Holds the name of property that is
     *                                     		requested. For which the fare quote
     * 											has to be obtained.
     *
     * @return AirfarePriceResponseData     	AirfarePriceResponseData the the value
     *                                     		of requested property having the fare quote.
     *
     * @throws SharedException 		            if any condition was encountered that
     *                               		    prevented the rollback of this EJB
     *                                     		transaction.
     * @roseuid 439FFEF20278
     * @J2EE_METHOD  --  getFareQuote
     */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE","RESERVATION_TRAVELAGENT_ROLE"})
	public AirfarePriceResponseData getFareQuote(
                final AirfarePriceRequestParam pAirfarePriceRequestParam) throws SharedException {		
    	final Object[] methodArgs = {pAirfarePriceRequestParam};
    	super.onMethodEntry("getFareQuote(AirPriceRequestParam)", methodArgs);
    	final AirfarePriceResponseData aAirPriceResponseData = new AirfarePriceResponseData();
    	try {
    	    final ItineraryParam aItineraryParam  = new FareService().getFareQuote(pAirfarePriceRequestParam);
    	    if(aItineraryParam.isOfferAgreementNotExists()){ 
    	    	aAirPriceResponseData.setStatusUnSuccessful();
    	    	aAirPriceResponseData.setReasonCode(FareReasonCodeConstants.OFFER_MANAGER_AGREEMENT_NOT_FOUND,
                        SharedConstants.FARE_APPLICATION);
				throw new SharedException(aAirPriceResponseData);
			}
    	    if(aItineraryParam.isErrorResponse()){ 
    	    	aAirPriceResponseData.setStatusUnSuccessful();
    	    	aAirPriceResponseData.setReasonCode(FareReasonCodeConstants.OFFER_MANAGER_REQUEST_FAILED,
                        SharedConstants.FARE_APPLICATION);
				throw new SharedException(aAirPriceResponseData);
			}
    	    if(aItineraryParam.isClassNotFound()){ 
    	    	aAirPriceResponseData.setStatusUnSuccessful();
    	    	aAirPriceResponseData.setReasonCode(FareReasonCodeConstants.OFFER_MANAGER_CLASS_NOT_FOUND,
                        SharedConstants.FARE_APPLICATION);
				throw new SharedException(aAirPriceResponseData);
			}
	    	aAirPriceResponseData.setItineraryParam(aItineraryParam);
	    	aAirPriceResponseData.setStatusSuccessful();
	    	
    	} catch (SharedException aSharedException) {
        	aAirPriceResponseData.setStatusUnSuccessful();
            aAirPriceResponseData.setAllFromAResponseData(aSharedException.getResponseData());
            SharedLogger.log(FareLogIdConstants.LOG_ID_1002, aSharedException, aAirPriceResponseData);
        } catch (Throwable aThrowable) {
        	aAirPriceResponseData.setStatusFatalWrite();
        	aAirPriceResponseData.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION, SharedConstants.FARE_APPLICATION);
        	aAirPriceResponseData.setDebugMessage("AirFareEJB: getFareQuote(AirfarePriceRequestParam):Unexpected Exception");
			SharedLogger.log(FareLogIdConstants.LOG_ID_1002, aThrowable, aAirPriceResponseData);
		} finally {
    		super.onMethodExit(aAirPriceResponseData);
		}
    	return aAirPriceResponseData;
	}




    /**
     * Retrieves the value of the specific property from the
     * <code>Aircore.properties</code> file.
     *
     * <p>
     * The method does the following actions in sequence
     * <ol>
     *
     * <li>
     * Does the Security check and ensures that the agent invoking this method
     * has access rights to do so.
     *
     * <li>
     * Invokes the {@link FareService#getLowestFareQuote} to retrive the
     * lowest fare quote.
     *
     * </ol>
     * </p>
     *
     * @param  pAirfarePriceRequestParam		Holds the name of property that is
     *                                     		requested. For which the lowest
     *                                     		fare quote has to be obtained.
     *
     * @return AirfarePriceResponseData     	AirfarePriceResponseData the the value
     *                                     		of requested property having the lowest
     * 											fare quote.
     *
     * @throws SharedException 		            if any condition was encountered that
     *                               		    prevented the rollback of this EJB
     *                                     		transaction.
     */

	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public AirfarePriceResponseData getLowestFareQuote(
                final AirfarePriceRequestParam pAirfarePriceRequestParam) throws SharedException {
		final Object[] methodArgs = {pAirfarePriceRequestParam };
		super.onMethodEntry("getLowestFareQuote(AirPriceRequestParam)", methodArgs);
    	final AirfarePriceResponseData aAirfarePriceResponseData = new AirfarePriceResponseData();
    	ItineraryParam aItineraryParam;
    	try {
    		aItineraryParam = new FareService().getLowestFareQuote(pAirfarePriceRequestParam);
    		aAirfarePriceResponseData.setItineraryParam(aItineraryParam);
    		aAirfarePriceResponseData.setStatusSuccessful();
    	}
        catch (SharedException aSharedException) {
            aAirfarePriceResponseData.setAllFromAResponseData(aSharedException.getResponseData());
    	}
    	finally {
    		// EJBExit calls once the EJB processing is over.
    		super.onMethodExit(aAirfarePriceResponseData);

    	}
    		return aAirfarePriceResponseData;
    	}


     /**
      * This method is used to retrieve Informative price list for given data
      * from the external fare system.
      *
      * This method accepts a
      * {@linkplain InformativePriceRequestDataParam InformativePriceRequestDataParam}
      * object which contains all details that is used to retrieve Informative price list.
      * <p>
      * The method does the following in sequence:<br>
      * 1. It invokes the method getInformativePriceList to get the response.
      * <b>Errors:</b><br>
      * An error has occurred if the response attribute setStatusSuccessful is set
      * to false. The response attribute reasonCode contains the error reason code
      * and the response attribute debugMessage contains the error text.
      * <p>
      * Possible reasonCodes that may be returned include: <br>
      * {@linkplain com.unisys.trans.aircore.far.constant.FareReasonCodeConstants#UNEXPECTED_EXCEPTION FareReasonCodeConstants.UNEXPECTED_EXCEPTION}<br>
  	  * @param  pInformativePriceRequestDataParam
      * <code>
      *      <ul>
      *      <li>Security Attributes - (M)<br>
      *          See Shared SharedSecurityParam
      *      <li>airportCode - (M)
      *      </ul>
      * </code>
      * @return InformativePriceResponseData
      * <code>
      *      <ul>
      *          <li>response attributes<br>
      *              See Shared ResponseData
      *      </ul>
      * </code>
      * @throws RemoteException
      * @throws SharedException EJBExit throws SharedException.
	  *
	  */
	@RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	 public InformativePriceResponseData getInformativePriceList(
            final InformativePriceRequestDataParam pInformativePriceRequestDataParam) throws SharedException,
            RemoteException {
        final Object[] methodArgs = { pInformativePriceRequestDataParam };

       
        InformativePriceResponseParam aInformativePriceResponseParam;
        final InformativePriceResponseData aInformativePriceResponseData = new InformativePriceResponseData();
        try {
            super.onMethodEntryValidate("getInformativePriceList(InformativePriceRequestDataParam)", methodArgs);
            aInformativePriceResponseParam = new FareService()
                    .getInformativePriceList(pInformativePriceRequestDataParam);
            aInformativePriceResponseData.setInformativePriceResponseParam(aInformativePriceResponseParam);
            aInformativePriceResponseData.setStatusSuccessful();
        }
        catch (SharedException aSharedException) {
            aInformativePriceResponseData.setStatusFatalReadOnly();
            aInformativePriceResponseData.setAllFromAResponseData(aSharedException.getResponseData());
        }
        finally {
            // EJBExit calls once the EJB processing is over.
            super.onMethodExit(aInformativePriceResponseData);
        }
        return aInformativePriceResponseData;
    }


    /**
	 * This method validates the mandatory attributes of the input object to
	 * GetFareRoutings.
	 * <p>
	 * The method
	 * {@linkplain AirFareEJB#GetFareRoutings AirFareEJB.GetFareRoutings} method
	 * will gets detail information about Fare Routings for requested fare basis
	 * code by the agent.
	 * <p>
	 * This method checks for the attributes of the input object. If the input
	 * object does not contain the mandatory attribute(s), then the status of
	 * the {@linkplain ResponseData ResponseData} will be set to
	 * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
	 *
	 * When the input object contains all the mandatory attributes, then the
	 * status of the <code>ResponseData</code> is set to
	 * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
	 *
	 * @param pOtherFareDetailsRequest
	 *            the <code>OtherFareDetailsRequest</code> contains object
	 *            that holds all the attributes required to hold the fare
	 *            response attributes obtained for the requested fare.
	 * @return ResponseData Returns <code>Successful</code> if the information
	 *         is valid, <code>Unsuccessful</code> otherwise.
	 */
	private ResponseData validateForGetFareRoutings(
			final OtherFareDetailsRequestParam pOtherFareDetailsRequest) {

		ResponseData aResponseData = new ResponseData();
		aResponseData.setStatusSuccessful();

		// FareBasisCode null check
		if (StringUtils.isEmpty(pOtherFareDetailsRequest.getFareBasisCode())) {

			aResponseData
					.setReasonCode(
							FareValidationReasonCodeConstants.FARE_BASIC_CODE_NOT_PRESENT,
							SharedConstants.FAR_VALIDATION_APPLICATION);
			aResponseData.setStatusFatalReadOnly();
			aResponseData
					.setDebugMessage("AirFareEJB.validateForGetFareRoutings() Fare Basis Code "
							+ "is not present, should not be null");
			SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1,
					aResponseData);
		}
			return aResponseData;
		}

	/**
     * This method validates the mandatory attributes to the input object of
     * the {@link #getFareAddOns <code>getFareAddOns</code>}method.
     * <p>
     * The method
     * {@linkplain AirFareEJB#getFareAddOns AirFareEJB.getFareAddOns}
     * gets detail information about Fare AddOns for the
     * fare basis code requested by the agent.
     * <p>
     * This method checks for the attributes of the input object. If the input
     * object does not contain the mandatory attribute(s), then the status of
     * the {@linkplain ResponseData ResponseData} will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
     *
     * @param pOtherFareDetailsRequest
     * @return ResponseData
     */
    private ResponseData validateForGetFareAddOns(final OtherFareDetailsRequestParam pOtherFareDetailsRequest) {

        ResponseData aResponseData = new ResponseData();

        // Null check of FareBasisCode
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getFareBasisCode())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_BASIC_CODE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareAddOns()-Fare Basis Code "
                            + "of Fare AddOns should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_4,aResponseData);
            return aResponseData;
        }

        // Null check of FirstTicketDate
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getFirstTicketDate())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FIRST_TICKET_DATE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareAddOns()-FirstTicketDate "
                            + "of Fare AddOns should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_5,aResponseData);
            return aResponseData;
        }

        // Null check of LastTicketDate
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getLastTicketDate())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.LAST_TICKET_DATE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareAddOns()-LastTicketDate "
                            + "of Fare AddOns should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_6,aResponseData);
            return aResponseData;
        }
        aResponseData.setStatusSuccessful();
        return aResponseData;
    }
    /**
     * This method validates the mandatory attributes to the input object of
     * the {@link #getFareReservation <code>getFareReservation</code>}method.
     * <p>
     * The method
     * {@link AirFareEJB#getFareReservation AirFareEJB.getFareReservation}
     * gets detail information about Fare Reservations for the
     * fare basis code requested by the agent.
     * <p>
     * This method checks for the attributes of the input object. If the input
     * object does not contain the mandatory attribute(s), then the status of
     * the {@linkplain ResponseData ResponseData} will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
     *
     * @param pOtherFareDetailsRequest
     * @return ResponseData
     */
    private ResponseData validateForGetFareReservation(
                final OtherFareDetailsRequestParam pOtherFareDetailsRequest) {

        ResponseData aResponseData = new ResponseData();

        // Null check of FareBasisCode
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getFareBasisCode())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_BASIC_CODE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareReservation()-Fare Basis Code "
                            + "of Fare Reservation should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_7,aResponseData);
            return aResponseData;
        }

        // Null check of FirstTicketDate
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getFirstTicketDate())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FIRST_TICKET_DATE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareReservation()-FirstTicketDate "
                            + "of Fare Reservation should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_8,aResponseData);
            return aResponseData;

        }

        // Null check of LastTicketDate
        if (StringUtils.isEmpty(pOtherFareDetailsRequest.getLastTicketDate())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.LAST_TICKET_DATE_NOT_PRESENT,
                            SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareReservation()-LastTicketDate "
                            + "of fare reservstion shouldn't be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_9,aResponseData);
            return aResponseData;
        }
        aResponseData.setStatusSuccessful();
        return aResponseData;
 }

    //*******************Validation Methods - Start************************


    /**
     * Validates for all the mandatory attributes when a agreement between
     * Transporting airline is requested. If the validation is successful the
     * request is sent to the SITA Fare System.The SITA Fare System returns a
     * list of SITA Agreement Groups for the requested agreement between
     * Transporting airline.
     *
     * <p>
     * The
     * {@link com.unisys.trans.aircore.far.param.SupportFareAndPricingParam}
     * object is validated for:
     * 1.Null checks of the attributes such
     * <code>TicketingAirline</code>,
     * <code>salesDate</code> and
     * <code>travelDate</code>.
     *
     * @param pSupportFareAndPricingParam Holds the attributes required to hold
     *            the SupportFareAndPricingRequest attributes.
     * @throws SharedException when the validation of the
     *             FareRuleParagraphRequest object fails.
     *
     */
    private void validateTransportingAirlinesRequest(
            final SupportFareAndPricingParam pSupportFareAndPricingParam)
            throws SharedException {
        String aTicketingAirline = "";
        String aSalesDate = "";
        String aTravelDate = "";
        SitaSupportFareAndPricingParam aSitaSupportFareAndPricingParam = null;
        if (pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) {
            aSitaSupportFareAndPricingParam = (SitaSupportFareAndPricingParam) pSupportFareAndPricingParam;
            // Gets the ticketing airline
            aTicketingAirline = aSitaSupportFareAndPricingParam.getTicketingAirline();
            // Gets the sales date
            aSalesDate = aSitaSupportFareAndPricingParam.getSalesDate();
            // Gets the travel date
            aTravelDate = aSitaSupportFareAndPricingParam.getTravelDate();

            if (StringUtils.isBlankString(aTicketingAirline)) {
                throw ExceptionCreator.createSharedException(
                    FareValidationReasonCodeConstants.INVALID_TICKETING_AIRLINE, SharedConstants.FATAL_READONLY_ERROR,
                    FareValidationLogIdConstants.LOG_ID_600752, SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateInterlineAgrRequest(): The Ticketing Airline code is invalid.");
            }

            if (StringUtils.isBlankString(aSalesDate)) {
                throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_SALES_DATE,
                    SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600754,
                    SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateInterlineAgrRequest(): The Sales Date is invalid.");
            }

            if (StringUtils.isBlankString(aTravelDate)) {
                throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_TRAVEL_DATE,
                    SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600755,
                    SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateInterlineAgrRequest(): The Travel Date is invalid.");
            }
        }

    }

    /**
     * Validates for all the mandatory attributes when Calculation of Mileage is
     * requested. If the validation is successful the request is sent to the
     * SITA Fare System.The SITA Fare System returns a list of Mileage
     * information for the requested Calculation of Mileage.
     *
     * <p>
     * The
     * {@link SupportFareAndPricingParam pSupportFareAndPricingParam}
     * object is validated for:
     * <p>
     * 1.Null checks of the attributes such
     * <code>origin</code>,
     * <code>destination</code> and
     * <code>airline</code>.
     * Possible reason codes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#INVALID_AIRLINE_CODE FareValidationReasonCodeConstants.INVALID_AIRLINE_CODE}
     * {@link FareValidationReasonCodeConstants#INVALID_DESTINATION FareValidationReasonCodeConstants.INVALID_DESTINATION}
     * {@link FareValidationReasonCodeConstants#INVALID_ORIGIN FareValidationReasonCodeConstants.INVALID_ORIGIN} <br>
     * </code>
     *
     * @param pSupportFareAndPricingParam Holds the attributes required to hold
     *            the SupportFareAndPricingRequest attributes.
     *
     * @return ResponseData
     *
     */
    private ResponseData validateForMileageCalculation(
    		final SupportFareAndPricingParam pSupportFareAndPricingParam) {

        String anOrigin;
        String anAirline;
        String aDestination;
        int aFlightSegListSize;
        SitaSupportFareAndPricingParam aSitaSupportFareAndPricingParam = null;
        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
        if (pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) {
            aSitaSupportFareAndPricingParam = (SitaSupportFareAndPricingParam) pSupportFareAndPricingParam;
            SitaFlightSegmentParam aSitaFlightSegmentParam;
            // Gets the SitaCalculateMileageParamRequest param
            List aFlightSegList = aSitaSupportFareAndPricingParam.getSitaCalculateMileageParamRequest()
                    .getSitaFlightSegmentList();

            if (aFlightSegList != null && aFlightSegList.size() > 0) {
                aFlightSegListSize = aFlightSegList.size();
                for (int i = 0; i < aFlightSegListSize; i++) {
                    aSitaFlightSegmentParam = (SitaFlightSegmentParam) aFlightSegList.get(i);
                    // Gets the origin
                    anOrigin = aSitaFlightSegmentParam.getOrigin();
                    // Gets the airline code
                    anAirline = aSitaFlightSegmentParam.getAirlineCode();
                    // Gets the destination
                    aDestination = aSitaFlightSegmentParam.getDestination();
                    if (StringUtils.isEmpty(anOrigin)) {
                    	aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_ORIGIN,
                                SharedConstants.FAR_VALIDATION_APPLICATION);
                            aResponseData.setStatusFatalReadOnly();
                            aResponseData.setDebugMessage("AirFareEJB.validateForMileageCalculation()- "
                                + "Origin should not be null");
                            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_807, aResponseData);
                            return aResponseData;
                    }

                    if (StringUtils.isEmpty(anAirline)) {
                    	aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_AIRLINE_CODE,
                                SharedConstants.FAR_VALIDATION_APPLICATION);
                            aResponseData.setStatusFatalReadOnly();
                            aResponseData.setDebugMessage("AirFareEJB.validateForMileageCalculation()- "
                                + "Airline code should not be null");
                            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_808, aResponseData);
                            return aResponseData;
                    }

                    if (StringUtils.isEmpty(aDestination)) {
                    	aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_DESTINATION,
                                SharedConstants.FAR_VALIDATION_APPLICATION);
                            aResponseData.setStatusFatalReadOnly();
                            aResponseData.setDebugMessage("AirFareEJB.validateForMileageCalculation()- "
                                + "Destination should not be null");
                            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_809, aResponseData);
                            return aResponseData;
                    }
                }
            }
        }
        return aResponseData;
    }

    /**
     * Validates for all the mandatory attributes when Tax Information is
     * requested. If the validation is successful the request is sent to the
     * SITA Fare System.The SITA Fare System returns a list of Tax Information.
     * <p>
     * The
     * {@link SupportFareAndPricingParam pSupportFareAndPricingParam}
     * object is validated for:
     * 1.Null checks of the attributes such
     * <code>country</code>, <code>countryTaxCode</code>, <code>cityOrAirport</code>,
     * <code>cityOrAirportTaxCode</code>, <code>airline</code>,
     * <code>airlineTaxCode</code>, <code>salesDate</code> and
     * <code>travelDate</code>.
     *
     * @param pSupportFareAndPricingParam Holds the attributes required to hold
     *            the SupportFareAndPricingRequest attributes.
     * @throws SharedException when the validation of the
     *             FareRuleParagraphRequest object fails.
     *
     */
    private void validateTaxInformationRequest(final SupportFareAndPricingParam pSupportFareAndPricingParam)
            throws SharedException {
        String aCountry;
        String aCountryTaxCode;
        String aCityOrAirport;
        String aCityOrAirportTaxCode;
        String aAirline;
        String aAirlineTaxCode;
        String aSalesDate;
        String aTravelDate;

        SitaSupportFareAndPricingParam aSitaSupportFareAndPricingParam = null;
        if (pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) {
            aSitaSupportFareAndPricingParam = (SitaSupportFareAndPricingParam) pSupportFareAndPricingParam;
            // Gets the tax code
            aCountryTaxCode = aSitaSupportFareAndPricingParam.getTaxCode();
            // Gets the airline code
            aAirlineTaxCode = aSitaSupportFareAndPricingParam.getAirlineCode();
            // Gets the sales date
            aSalesDate = aSitaSupportFareAndPricingParam.getSalesDate();
            // Gets the travel date
            aTravelDate = aSitaSupportFareAndPricingParam.getTravelDate();
            // Gets the country code
            aCountry = aSitaSupportFareAndPricingParam.getCountryCode();
            // Gets the city or airport code
            aCityOrAirport = aSitaSupportFareAndPricingParam.getAirportCode();
            // Gets the tax code
            aCityOrAirportTaxCode = aSitaSupportFareAndPricingParam.getTaxCode();
            // Gets the airline code
            aAirline = aSitaSupportFareAndPricingParam.getAirlineCode();

            if (StringUtils.isBlankString(aCountry)) {
                throw ExceptionCreator.createSharedException(
                    FareValidationReasonCodeConstants.INVALID_COUNTRY, SharedConstants.FATAL_READONLY_ERROR,
                    FareValidationLogIdConstants.LOG_ID_600773, SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Country Tax Code is invalid.");
            }

            if (StringUtils.isBlankString(aCountryTaxCode)) {
                throw ExceptionCreator.createSharedException(
                    FareValidationReasonCodeConstants.INVALID_COUNTRY_TAX_CODE, SharedConstants.FATAL_READONLY_ERROR,
                    FareValidationLogIdConstants.LOG_ID_600762, SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Country Tax Code is invalid.");
            }

            if (StringUtils.isBlankString(aCityOrAirport)) {
                throw ExceptionCreator
                        .createSharedException(FareValidationReasonCodeConstants.INVALID_CITY_OR_AIRPORT,
                            SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600764,
                            SharedConstants.LOG_ERROR, "",
                            "AirFareEJB().validateTaxInformationRequest(): The City Or Airport Tax Code is invalid.");
            }

            if (StringUtils.isBlankString(aCityOrAirportTaxCode)) {
                throw ExceptionCreator
                        .createSharedException(FareValidationReasonCodeConstants.INVALID_CITY_TAX_CODE,
                            SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600763,
                            SharedConstants.LOG_ERROR, "",
                            "AirFareEJB().validateTaxInformationRequest(): The City Or Airport Tax Code is invalid.");
            }


            if (StringUtils.isBlankString(aAirline)) {
                throw ExceptionCreator.createSharedException(
                    FareValidationReasonCodeConstants.INVALID_AIRLINE_CODE, SharedConstants.FATAL_READONLY_ERROR,
                    FareValidationLogIdConstants.LOG_ID_600765, SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Airline Tax Code is invalid.");
            }

            if (StringUtils.isBlankString(aAirlineTaxCode)) {
                throw ExceptionCreator.createSharedException(
                    FareValidationReasonCodeConstants.INVALID_AIRLINE_TAX_CODE, SharedConstants.FATAL_READONLY_ERROR,
                    FareValidationLogIdConstants.LOG_ID_600764, SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Airline Tax Code is invalid.");
            }

            if (StringUtils.isBlankString(aSalesDate)) {
                throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_SALES_DATE,
                    SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600754,
                    SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Sales Date is invalid.");
            }

            if (StringUtils.isBlankString(aTravelDate)) {
                throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_TRAVEL_DATE,
                    SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600755,
                    SharedConstants.LOG_ERROR, "",
                    "AirFareEJB().validateTaxInformationRequest(): The Travel Date is invalid.");
            }
        }
    }

    /**
     * Validates for all the mandatory attributes when a Currency Conversion is
     * requested for the specified amount. If the validation is successful the
     * request is sent to the SITA Fare System.The SITA Fare System returns all
     * the details about the conversion of currency for the requested amount.
     *
     * <p>
     * The {@link SupportFareAndPricingParam pSupportFareAndPricingParam} object
     * is validated for:
     * 1.Null checks of the attributes such as
     * <code>rateType</code>, <code>rateDate</code>, <code>amount</code>,
     * <code>fromCurrency</code>, <code>toCurrency</code>,
     * <code>ticketDate<code>, <code>date</code>, <code>currency</code>,
     * <code>country</code> and <code>airport</code>.
     *
     * @param pSupportFareAndPricingParam Holds the attributes required to hold
     *            the SupportFareAndPricingRequest attributes.
     * @throws SharedException When the validation of the
     *             FareRuleParagraphRequest object fails.
     *
     */
    private void validateCurrencyConversionRequest(
            final SupportFareAndPricingParam pSupportFareAndPricingParam)
            throws SharedException {
        String aRateType;
        String aRateDate;
        double aAmount;
        String aFromCurrency;
        String aToCurrency;
        String aticketDate;
        String aDate;
        String aCurrency;
        String aCountry;
        String aCityOrAirport;

        SitaSupportFareAndPricingParam aSitaSupportFareAndPricingParam = null;
        if (pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) {
            aSitaSupportFareAndPricingParam = (SitaSupportFareAndPricingParam) pSupportFareAndPricingParam;
            // Gets the rate type
            aRateType = aSitaSupportFareAndPricingParam.getRateType();
            // Gets the rate date
            aRateDate = aSitaSupportFareAndPricingParam.getRateDate();
            // Gets the amount
            aAmount = aSitaSupportFareAndPricingParam.getAmount();
            // Gets the from currency
            aFromCurrency = aSitaSupportFareAndPricingParam.getFromCurrency();
            // Gets the to currency
            aToCurrency = aSitaSupportFareAndPricingParam.getToCurrency();
            // Gets theticket date
            aticketDate = aSitaSupportFareAndPricingParam.getTicketDate();
            // Gets the date
            aDate = aSitaSupportFareAndPricingParam.getRateDate();
            // Gets the currency
            aCurrency = aSitaSupportFareAndPricingParam.getCurrency();
            // Gets the country code
            aCountry = aSitaSupportFareAndPricingParam.getCountryCode();
            // Gets the airport code
            aCityOrAirport = aSitaSupportFareAndPricingParam.getAirportCode();

              if (StringUtils.isBlankString(aRateType)) {
                  throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_RATE_TYPE,
                      SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600756,
                      SharedConstants.LOG_ERROR, "",
                      "AirFareEJB().validateCurrencyConversionRequest(): The Rate Type is invalid.");
              }

              if (StringUtils.isBlankString(aRateDate)) {
                  throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_RATE_DATE,
                      SharedConstants.FATAL_READONLY_ERROR,FareValidationLogIdConstants.LOG_ID_600757, SharedConstants.LOG_ERROR, "",
                      "AirFareEJB().validateCurrencyConversionRequest(): The Rate Date is invalid.");
              }

              if (aAmount == 0) {
                  throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_AMOUNT,
                      SharedConstants.FATAL_READONLY_ERROR,FareValidationLogIdConstants.LOG_ID_600758, SharedConstants.LOG_ERROR, "",
                      "AirFareEJB().validateCurrencyConversionRequest(): The specified Amount is invalid.");
              }

              if (StringUtils.isBlankString(aFromCurrency)) {
                  throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_FROM_CURRENCY,
                      SharedConstants.FATAL_READONLY_ERROR, FareValidationLogIdConstants.LOG_ID_600759,
                            SharedConstants.LOG_ERROR, "",
                      "AirFareEJB().validateCurrencyConversionRequest(): The From Currency Code is invalid.");
              }

              if (StringUtils.isBlankString(aToCurrency)) {
                  throw ExceptionCreator.createSharedException(FareValidationReasonCodeConstants.INVALID_TO_CURRENCY,
                      SharedConstants.FATAL_READONLY_ERROR,FareValidationLogIdConstants.LOG_ID_600760, SharedConstants.LOG_ERROR, "",
                      "AirFareEJB().validateCurrencyConversionRequest(): The To Currency Code is invalid.");
              }
        }

    }

    //*******************Validation Methods - End************************

    /**
     * This method validates whether all the mandatory and conditional
     * attributes are present in the
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}
     * object, to complete the process of
     * <code>AirFareEJB.getMileageSurcharge</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getMileageSurcharge(SupportFareAndPricingParam) AirFare.getMileageSurcharge}
     * method is used to retrieve Passenger Facility Charge Information for a
     * given <code>airportCode</code> from the external fare system.
     * <p>
     * If <code>SupportFareAndPricingParam</code> object does not contain
     * the mandatory attribute or conditional attribute to complete the
     * process, then the status of the {@linkplain ResponseData ResponseData}
     * will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reason codes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#ORIGIN_NOT_PRESENT FareValidationReasonCodeConstants.ORIGIN_NOT_PRESENT} <br>
     * {@link FareValidationReasonCodeConstants#DESTINATION_NOT_PRESENT FareValidationReasonCodeConstants.DESTINATION_NOT_PRESENT}
     * </code>
     *
     * @param pSupportFareAndPricingParam
     * @return ResponseData
     */
    private ResponseData validateForGetMileageSurcharge(SupportFareAndPricingParam
                pSupportFareAndPricingParam) {
        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
        // Null check of Origin
        if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam)) {
        if (StringUtils.isEmpty(pSupportFareAndPricingParam.getOrigin())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.ORIGIN_NOT_PRESENT,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetMileageSurcharge()- Origin "
                        + "should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1201, aResponseData);
            return aResponseData;
        }

        // Null check of Destination
        if (StringUtils.isEmpty(pSupportFareAndPricingParam.getDestination())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.DESTINATION_NOT_PRESENT,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetMileageSurcharge()- Destination "
                        + "should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1202, aResponseData);
            return aResponseData;
        }
        }
        return aResponseData;
    }

    /**
     * This method validates whether all the mandatory and conditional
     * attributes are present in the
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}
     * object, to complete the process of
     * <code>AirFareEJB.getPassengerFacilityCharge</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getPassengerFacilityCharge(SupportFareAndPricingParam) AirFare.getPassengerFacilityCharge}
     * method is used to retrieve Passenger Facility Charge Information for a
     * given <code>airportCode</code> from the external fare system.
     * <p>
     * If <code>SupportFareAndPricingParam</code> object does not contain
     * the mandatory attribute or conditional attribute to complete the
     * process, then the status of the {@linkplain ResponseData ResponseData}
     * will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reason codes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#AIRPORT_NOT_PRESENT FareValidationReasonCodeConstants.AIRPORT_NOT_PRESENT}
     * </code>
     *
     * @param pSupportFareAndPricingParam
     * @return ResponseData
     */
    private ResponseData validateForGetPassengerFacilityCharge(
                final SupportFareAndPricingParam pSupportFareAndPricingParam) {
        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
        if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) &&
                    StringUtils.isEmpty(pSupportFareAndPricingParam.getAirportCode())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.AIRPORT_NOT_PRESENT,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetPassengerFacilityCharge()- AirportCode "
                        + "should not be null");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1203, aResponseData);
            return aResponseData;
        }
        return aResponseData;
    }

    /**
     * This method validates whether the
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}
     * object contains all the mandatory fields and conditional fields to
     * complete the process of <code>AirFareEJB.getExcessBaggageCharge</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getExcessBaggageCharge AirFare.getExcessBaggageCharge}
     * gets the excess baggage charges. It accepts an object of
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}.
     * <p>
     * If <code>SupportFareAndPricingParam</code> object does not contain the
     * mandatory attribute or conditional attribute to complete the process,
     * then the status of the {@linkplain ResponseData ResponseData} will be
     * set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the attributes mandatory, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reasonCodes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#AIRLINE_CODE_EMPTY FareValidationReasonCodeConstants.AIRLINE_CODE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#ORIGIN_EMPTY FareValidationReasonCodeConstants.ORIGIN_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#STOP_OVER_CITY_OR_AIRPORT_EMPTY FareValidationReasonCodeConstants.STOP_OVER_CITY_OR_AIRPORT_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#STOP_OVER_LIST_EMPTY FareValidationReasonCodeConstants.STOP_OVER_LIST_EMPTY}
     * </code>
     *
     * @param pSupportFareAndPricingParam
     * SupportFareAndPricingParam object
     * @return ResponseData
     * @see AirFare#getExcessBaggageCharge
     */
    private ResponseData validateForGetExcessBaggageCharge(
                final SupportFareAndPricingParam pSupportFareAndPricingParam) {

        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();

        //OTA flow validations
        if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam)) {
            //Validation for airline code
            if (StringUtils.isEmpty(pSupportFareAndPricingParam.getAirlineCode())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.AIRLINE_CODE_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetExcessBaggageCharge(): Airline " +
                        "code is mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1001, aResponseData);
                return aResponseData;
            }

            // Validation for origin
            if (StringUtils.isEmpty(pSupportFareAndPricingParam.getOrigin())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.ORIGIN_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetExcessBaggageCharge(): Origin is "
                            + "mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1002, aResponseData);
                return aResponseData;
            }

            // Validation for stopOverList
            List aStopOverList = pSupportFareAndPricingParam.getStopOverList();
            if (aStopOverList == null || aStopOverList.isEmpty()) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.STOP_OVER_LIST_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetExcessBaggageCharge(): Stop over " +
                        "list is mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1003, aResponseData);
                return aResponseData;
            }

            //Validation for stop over city/ airport code
            final int iMax = aStopOverList.size();
            String aCityOrAirportCode = null;
            for (int i = 0; i < iMax; i++) {
                aCityOrAirportCode = (String) aStopOverList.get(i);
                if (StringUtils.isEmpty(aCityOrAirportCode)) {
                    aResponseData.setReasonCode(
                        FareValidationReasonCodeConstants.STOP_OVER_CITY_OR_AIRPORT_EMPTY,
                        SharedConstants.FAR_VALIDATION_APPLICATION);
                    aResponseData.setStatusFatalReadOnly();
                    aResponseData.setDebugMessage("AirFareEJB.validateForGetExcessBaggageCharge(): Stop " +
                            "over city or airport code is mandatory.");
                    SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1007, aResponseData);
                    return aResponseData;
                }
            }
        }
        return aResponseData;
    }

    /**
     * This method validates whether the
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}
     * object contains all the mandatory fields and conditional fields to
     * complete the process of <code>AirFareEJB.getExcessBaggageCharge</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getInterlineAgreements AirFare.getInterlineAgreements}
     * gets the interline agreements information. It accepts an object of
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}.
     * <p>
     * If <code>SupportFareAndPricingParam</code> object does not contain the
     * mandatory attribute or conditional attribute to complete the process,
     * then the status of the {@linkplain ResponseData ResponseData} will be
     * set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the attributes mandatory, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reasonCode that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#AIRLINE_CODE_EMPTY FareValidationReasonCodeConstants.AIRLINE_CODE_EMPTY}
     * </code>
     *
     * @param pSupportFareAndPricingParam
     * SupportFareAndPricingParam object
     * @return ResponseData
     * @see AirFare#getInterlineAgreements
     */
    private ResponseData validateForGetInterlineAgreements(
                                              final SupportFareAndPricingParam pSupportFareAndPricingParam) {
        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();

        //OTA flow validation
            if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) &&
                        StringUtils.isEmpty(pSupportFareAndPricingParam.getAirlineCode())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.AIRLINE_CODE_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetInterlineAgreements(): Airline " +
                        "code is mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1004, aResponseData);
            }
        return aResponseData;
    }

    /**
     * This method validates whether the
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}
     * object contains all the mandatory fields and conditional fields to
     * complete the process of <code>AirFareEJB.getMileageByGlobalInd</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getMileageByGlobalInd AirFare.getMileageByGlobalInd}
     * gets the mileage information by global indicator. It accepts an object of
     * {@linkplain SupportFareAndPricingParam SupportFareAndPricingParam}.
     * <p>
     * If <code>SupportFareAndPricingParam</code> object does not contain the
     * mandatory attribute or conditional attribute to complete the process,
     * then the status of the {@linkplain ResponseData ResponseData} will be
     * set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the attributes mandatory, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reasonCodes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#DESTINATION_EMPTY FareValidationReasonCodeConstants.DESTINATION_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#ORIGIN_EMPTY FareValidationReasonCodeConstants.ORIGIN_EMPTY}
     * </code>
     *
     * @param pSupportFareAndPricingParam
     * SupportFareAndPricingParam object
     * @return ResponseData
     * @see AirFare#getMileageByGlobalInd
     */
    private ResponseData validateForGetMileageByGlobalInd(
                final SupportFareAndPricingParam pSupportFareAndPricingParam) {

        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();

        //OTA flow validations
        if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam)) {
            // Validation for origin
            if (StringUtils.isEmpty(pSupportFareAndPricingParam.getOrigin())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.ORIGIN_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetMileageByGlobalInd(): Origin is " +
                        "mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1005, aResponseData);
                return aResponseData;
            }

            // Validation for destination
            if (StringUtils.isEmpty(pSupportFareAndPricingParam.getDestination())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.DESTINATION_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetMileageByGlobalInd(): Destination " +
                        "is mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1006, aResponseData);
                return aResponseData;
            }
        }
        return aResponseData;
    }

    /**
     * This method validates whether the
     * {@linkplain FareCategoryRequestParam FareCategoryRequest} object
     * contains all the mandatory fields and conditional fields to
     * complete the process of <code>AirFareEJB.getFareCategories</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getFareCategories AirFare.getFareCategories}
     * gets the fare categories . It accepts an object of
     * {@linkplain FareCategoryRequestParam FareCategoryRequest}.
     * <p>
     * If <code>FareCategoryRequest</code> object does not contain the
     * mandatory attribute or conditional attribute to complete the process,
     * then the status of the {@linkplain ResponseData ResponseData} will be
     * set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the attributes mandatory, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reasonCodes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#FARE_BASIS_CODE_EMPTY FareValidationReasonCodeConstants.FARE_BASIC_CODE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FARE_CATEGORY_INFO_LIST_EMPTY FareValidationReasonCodeConstants.FARE_CATEGORY_INFO_LIST_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FARE_CATEGORY_NAME_EMPTY FareValidationReasonCodeConstants.FARE_CATEGORY_NAME_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FARE_CATEGORY_PARAM_EMPTY FareValidationReasonCodeConstants.FARE_CATEGORY_PARAM_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FIRST_TICKET_DATE_EMPTY FareValidationReasonCodeConstants.FIRST_TICKET_DATE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#LAST_TICKET_DATE_EMPTY FareValidationReasonCodeConstants.LAST_TICKET_DATE_EMPTY}
     * </code>
     *
     * @param pFareCategoryRequest
     * FareCategoryRequest object
     * @return ResponseData
     * @see AirFare#getFareCategories
     */
    private ResponseData validateForGetFareCategories(final FareCategoryRequestParam pFareCategoryRequest) {

        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();

        //Validation for FareCategoryParam
        FareCategoryParam aFareCategoryParam = pFareCategoryRequest.getFareCategoryParam();
        if (aFareCategoryParam == null) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_CATEGORY_PARAM_EMPTY,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareCategories(): FareCategoryParam is " +
                    "mandatory.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1008, aResponseData);
            return aResponseData;
        }

        //Validation for fare basis code
        if (StringUtils.isEmpty(aFareCategoryParam.getFareBasisCode())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_BASIS_CODE_EMPTY,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareCategories(): Fare basis code is " +
                    "mandatory.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1009, aResponseData);
            return aResponseData;
        }

        //Validation for fareCategoryInformationParamList
        List aFareCategoryInformationParamList = aFareCategoryParam.getFareCategoryInformationParamList();
        if (aFareCategoryInformationParamList == null || aFareCategoryInformationParamList.isEmpty()) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_CATEGORY_INFO_LIST_EMPTY,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareCategories(): Fare category " +
                    "information list is mandatory.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1012, aResponseData);
            return aResponseData;
        }

        final int iMax = aFareCategoryInformationParamList.size();
        FareCategoryInformationParam aFareCategoryInformationParam = null;
        for (int i = 0; i < iMax; i++) {
            aFareCategoryInformationParam =
                                    (FareCategoryInformationParam) aFareCategoryInformationParamList.get(i);
            //Validation for category name
            if (StringUtils.isEmpty(aFareCategoryInformationParam.getCategoryName())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_CATEGORY_NAME_EMPTY,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetFareCategories(): Fare category " +
                        "name is mandatory.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1013, aResponseData);
                return aResponseData;
            }
        }
        return aResponseData;
    }

    /**
     * This method validates whether the
     * {@linkplain FareRuleParagraphRequestParam FareRuleParagraphRequest}
     * object contains all the mandatory fields and conditional fields to
     * complete the process of <code>AirFareEJB.getFareRuleParagraphs</code>.
     * <p>
     * The method
     * {@linkplain AirFare#getFareRuleParagraphs AirFare.getFareRuleParagraphs}
     * gets the fare categories . It accepts an object of
     * {@linkplain FareRuleParagraphRequestParam FareRuleParagraphRequest}.
     * <p>
     * If <code>FareRuleParagraphRequest</code> object does not contain the
     * mandatory attribute or conditional attribute to complete the process,
     * then the status of the {@linkplain ResponseData ResponseData} will be
     * set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the attributes mandatory, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}.
     * <p>
     * Possible reasonCodes that may be returned include:<br>
     * <code>
     * {@link FareValidationReasonCodeConstants#FARE_BASIS_CODE_EMPTY FareValidationReasonCodeConstants.FARE_BASIC_CODE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FARE_RULE_PARAGRAPH_LIST_EMPTY FareValidationReasonCodeConstants.FARE_RULE_PARAGRAPH_LIST_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FARE_RULE_PARAM_EMPTY FareValidationReasonCodeConstants.FARE_RULE_PARAM_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#FIRST_TICKET_DATE_EMPTY FareValidationReasonCodeConstants.FIRST_TICKET_DATE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#LAST_TICKET_DATE_EMPTY FareValidationReasonCodeConstants.LAST_TICKET_DATE_EMPTY}<br>
     * {@link FareValidationReasonCodeConstants#PARAGRAPH_NUMBER_NULL FareValidationReasonCodeConstants.PARAGRAPH_NUMBER_NULL}<br>
     * {@link FareValidationReasonCodeConstants#RULE_PARAGRAPH_NAME_EMPTY FareValidationReasonCodeConstants.RULE_PARAGRAPH_NAME_EMPTY}
     * </code>
     *
     * @param pFareRuleParagraphRequest
     * FareRuleParagraphRequest object
     * @return ResponseData
     * @see AirFare#getFareRuleParagraphs
     */
    private ResponseData validateForGetFareRuleParagraphs(
                                                final FareRuleParagraphRequestParam pFareRuleParagraphRequest) {
        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();

        //Validation for FareRuleParam
        FareRuleParam aFareRuleParam = pFareRuleParagraphRequest.getFareRuleParam();
        if (aFareRuleParam == null) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_RULE_PARAM_EMPTY,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareRuleParagraphs(): FareRuleParam is " +
                    "mandatory.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1014, aResponseData);
            return aResponseData;
        }

        //Validation for fare basis code
        if (StringUtils.isEmpty(aFareRuleParam.getFareBasisCode())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_BASIS_CODE_EMPTY,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetFareRuleParagraphs(): Fare basis code " +
                    "is mandatory.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_1015, aResponseData);
            return aResponseData;
        }
        return aResponseData;
    }

    /**
     * This method validates the mandatory attributes to the input object of the
     * {@link #getFares <code>getFares</code>}method.
     * <p>
     * The method {@link AirFareEJB#getFares AirFareEJB.getFares} gets detail
     * information about Fares for the requested fare type and fare details by
     * the user.
     * <p>
     * This method checks for the attributes of the input object. If the input
     * object does not contain the mandatory attribute(s), then the status of
     * the {@linkplain ResponseData ResponseData} will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * <p>
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
     *
     * @param pFareRequest
     * @return ResponseData
     */
    private ResponseData validateForGetFares(final FareRequestParam pFareRequest) {
        final ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
        FareInformationParam aFareInformationParam = null;
            // When FareInformationParam is null throws error.
            if (pFareRequest.getFareInformationParam() == null) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_INFORMATION_PARAM_NULL,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData
                        .setDebugMessage("AirFareEJB.validateForGetFares()- FareInformationParam is null.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_19, aResponseData);
                return aResponseData;
            }
            aFareInformationParam = pFareRequest.getFareInformationParam();
            // Destination cannot be empty.
            if (StringUtils.isEmpty(aFareInformationParam.getDestination())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_DESTINATION_FORMAT,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData
                        .setDebugMessage("AirFareEJB.validateForGetFares()- Destination detail is empty.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_14, aResponseData);
                return aResponseData;
            }
            // Origin cannot be empty.
            if (StringUtils.isEmpty(aFareInformationParam.getOrigin())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_ORIGIN_FORMAT,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetFares()- Origin detail is empty.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_15, aResponseData);
                return aResponseData;
            }
            // Fare display order cannot be null.
            if (StringUtils.isEmpty(aFareInformationParam.getFareDisplayOrder())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.FARE_DISPLAY_ORDER_NULL,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData
                        .setDebugMessage("AirFareEJB.validateForGetFares()-Fare display order is not given.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_16, aResponseData);
                return aResponseData;
            }

        return aResponseData;
    }

   /**
    * This method validates the mandatory attributes to the input object of
    * the {@link #getCurrencyConversion <code>getCurrencyConversion</code>}
    * method.
    * <p>
    * The method
    * {@link AirFareEJB#getCurrencyConversion AirFareEJB.getCurrencyConversion}
    * gets information about the amount converted from one Currecy type
    * to another type of Currency.
    * <p>
    * This method checks for the attributes of the input object. If the input
    * object does not contain the mandatory attribute(s), then the status of
    * the {@linkplain ResponseData ResponseData} will be set to
    * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
    * <p>
    * When the input object contains all the mandatory attributes, then the
    * status of the <code>ResponseData</code> is set to
    * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
    *
    * @param pSupportFareAndPricingParam
    * @return ResponseData
    * @throws SharedException
    */
   private ResponseData validateForGetCurrencyConversion(
           final SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {
        final ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
        if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam)) {
        if (pSupportFareAndPricingParam.getAmount() == 0) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_AMOUNT,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetCurrencyConversion()- Amount for the "

                    + "currecy conversion is not provided.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_20, aResponseData);
            return aResponseData;
        }
        if (StringUtils.isEmpty(pSupportFareAndPricingParam.getToCurrency())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_CURRENCY_CODE,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetCurrencyConversion()- To which "
                    + "currency type the conversion should be transferred is not present.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_21, aResponseData);
            return aResponseData;
        }
        if (StringUtils.isEmpty(pSupportFareAndPricingParam.getFromCurrency())) {
            aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_CURRENCY_CODE,
                SharedConstants.FAR_VALIDATION_APPLICATION);
            aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetCurrencyConversion()- From which "
                    + "currency type the conversion should be transferred is not given.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_22, aResponseData);
            return aResponseData;
            }
        }
        if (!StringUtils.isEmpty((pSupportFareAndPricingParam.getFromCurrency()))) {
        	new SharedGateway().validateCurrencyCode(pSupportFareAndPricingParam.getFromCurrency(),
        			pSupportFareAndPricingParam.getAgentAirlineDesignator());
       	}
        if (!StringUtils.isEmpty((pSupportFareAndPricingParam.getToCurrency()))) {
        	new SharedGateway().validateCurrencyCode(pSupportFareAndPricingParam.getToCurrency(),
        			pSupportFareAndPricingParam.getAgentAirlineDesignator());
        }
        return aResponseData;
    }

  /**
    * This method validates if the from currency and to currency are same.
    *
    * @param pSupportFareAndPricingParam
    */
    private void validateDuplicateCurrency(final SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {
        if (pSupportFareAndPricingParam.getToCurrency().equalsIgnoreCase(pSupportFareAndPricingParam.getFromCurrency())) {
            throw ExceptionCreator.createSharedException(
                FareReasonCodeConstants.DUPLICATE_CURRENCY,
                SharedConstants.FATAL_READONLY_ERROR, FareLogIdConstants.LOG_ID_600771,
                SharedConstants.LOG_ERROR, "",
                "AirFareEJB().validateDuplicateCurrency(): The from and to currency code is invalid.");

        }
    }

	/**
	 * This method validates the mandatory attributes of the input object to
	 * GetTaxInformation.
	 * <p>
	 * The method
	 * {@linkplain AirFareEJB#GetTaxInformation AirFareEJB.GetTaxInformation}
	 * method will be used to Get TaxInformation .
	 * <p>
	 * This method checks for the attributes of the input object. If the input
	 * object does not contain the mandatory attribute(s), then the status of
	 * the {@linkplain ResponseData ResponseData} will be set to
	 * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
	 *
	 * When the input object contains all the mandatory attributes, then the
	 * status of the <code>ResponseData</code> is set to
	 * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
	 *
	 * @param pSupportFareAndPricingParam
	 *            the <code>SupportFareAndPricingParam</code> that holds all
	 *            the information about the Support fare and pricing param.
	 * @return ResponseData Returns <code>Successful</code> if the information
	 *         is valid, <code>Unsuccessful</code> otherwise.
	 */
	private ResponseData validateForGetTaxInformation(
                final SupportFareAndPricingParam pSupportFareAndPricingParam) {

        ResponseData aResponseData = new ResponseData();
        aResponseData.setStatusSuccessful();
            if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) &&
                        StringUtils.isEmpty(pSupportFareAndPricingParam.getCountryCode())) {

                aResponseData.setReasonCode(FareValidationReasonCodeConstants.COUNTRY_CODE_NOT_PRESENT,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetTaxInformation() "
                        + "Country Code in not present, should not be null");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_251, aResponseData);
        }
        return aResponseData;
    }

    /**
     * This method validates the mandatory attributes to the input object of
     * the {@link #getInformativePriceList <code>getInformativePriceList</code>}method.
     * <p>
     * The method
     * {@link AirFareEJB#getInformativePriceList AirFareEJB.getInformativePriceList}
     * Retrieves the value of the specific property from the
     * <code>Aircore.properties</code> file.
     * <p>
     * This method checks for the attributes of the input object. If the input
     * object does not contain the mandatory attribute(s), then the status of
     * the {@linkplain ResponseData ResponseData} will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
     *
     * @param pInformativePriceRequestParam
     * @return ResponseData
     */
    private ResponseData validateForGetInformativePriceList(
    		final InformativePriceRequestParam pInformativePriceRequestParam) {
    	ResponseData aResponseData = new ResponseData();

    	if(StringUtils.isEmpty(pInformativePriceRequestParam.getDepartureDate())) {
    		aResponseData.setReasonCode(FareValidationReasonCodeConstants.DEPATURE_DATE_NULL,
    				SharedConstants.FAR_VALIDATION_APPLICATION);
    		aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetInformativePriceList():The Departure " +
            		" Date is either null or empty.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_801, aResponseData);
    		return aResponseData;
    	}
    	if(StringUtils.isEmpty(pInformativePriceRequestParam.getDestination())) {
    		aResponseData.setReasonCode(FareValidationReasonCodeConstants.DESTINATION_DETAILS_NOT_FOUND,
    				SharedConstants.FAR_VALIDATION_APPLICATION);
    		aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetInformativePriceList():The Destination" +
            		" is either null or empty.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_802, aResponseData);
    		return aResponseData;
    	}
    	if(StringUtils.isEmpty(pInformativePriceRequestParam.getOrigin())) {
    		aResponseData.setReasonCode(FareValidationReasonCodeConstants.ORIGIN_NOT_PRESENT,
    				SharedConstants.FAR_VALIDATION_APPLICATION);
    		aResponseData.setStatusFatalReadOnly();
            aResponseData.setDebugMessage("AirFareEJB.validateForGetInformativePriceList():The Origin" +
            		" is either null or empty.");
            SharedLogger.log(FareValidationLogIdConstants.LOG_ID_803, aResponseData);
    		return aResponseData;
    	}

    	aResponseData.setStatusSuccessful();
    	return aResponseData;
    }

	/**
     * This method validates the mandatory attributes to the input object of
     * the {@link #getRateOfExchange <code>getRateOfExchange</code>}method.
     * <p>
     * The method
     * {@link AirFareEJB#getRateOfExchange AirFareEJB.getRateOfExchange}
     * Retrieves the rate of exchange information.
     * <p>
     * This method checks for the attributes of the input object. If the input
     * object does not contain the mandatory attribute(s), then the status of
     * the {@linkplain ResponseData ResponseData} will be set to
     * {@linkplain SharedConstants#FATAL_READONLY_ERROR SharedConstants.FATAL_READONLY_ERROR}.
     * When the input object contains all the mandatory attributes, then the
     * status of the <code>ResponseData</code> is set to
     * {@linkplain SharedConstants#RESULT_SUCCESSFUL SharedConstants.RESULT_SUCCESSFUL}
     *
     * @param pSupportFareAndPricingParam
     * @return ResponseData
	 * @throws SharedException
     */
    private ResponseData validateForGetRateOfExchange(
                final SupportFareAndPricingParam pSupportFareAndPricingParam) throws SharedException {

        ResponseData aResponseData = new ResponseData();
            if (!(pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) &&
                        StringUtils.isEmpty(pSupportFareAndPricingParam.getCountryCode())) {
                aResponseData.setReasonCode(FareValidationReasonCodeConstants.INVALID_COUNTRY_CODE,
                    SharedConstants.FAR_VALIDATION_APPLICATION);
                aResponseData.setStatusFatalReadOnly();
                aResponseData.setDebugMessage("AirFareEJB.validateForGetRateOfExchange():The Country code in"
                        + " support and fare pricing param is null or empty.");
                SharedLogger.log(FareValidationLogIdConstants.LOG_ID_806, aResponseData);
            }
        if (pSupportFareAndPricingParam instanceof SitaSupportFareAndPricingParam) {
            SitaSupportFareAndPricingParam aSitaSupportFareAndPricingParam
                        = (SitaSupportFareAndPricingParam) pSupportFareAndPricingParam;
            if (!StringUtils.isEmpty((aSitaSupportFareAndPricingParam.getCurrency()))) {
                new SharedGateway().validateCurrencyCode(aSitaSupportFareAndPricingParam.getCurrency(),
            			pSupportFareAndPricingParam.getAgentAirlineDesignator());
           	}
        }
        aResponseData.setStatusSuccessful();
        return aResponseData;
    }

    
   
    @RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public AirfarePriceResponseData getRepriceResponse(
                final AirfarePriceRequestParam pAirfarePriceRequestParam) throws SharedException {
    	final Object[] methodArgs = {pAirfarePriceRequestParam};
    	super.onMethodEntry("getRepriceResponse(AirPriceRequestParam)", methodArgs);
    	final AirfarePriceResponseData aAirPriceResponceData = new AirfarePriceResponseData();
    	
    	try {
    	    final FareRepricerResponseParam aFareRepricerResponse  = new FareService().getRepriceResponse(pAirfarePriceRequestParam);
	    	aAirPriceResponceData.setFareRepricerResponse(aFareRepricerResponse);
	    	aAirPriceResponceData.setStatusSuccessful();
	    	
    	}
        catch (SharedException aSharedException) {
        	aAirPriceResponceData.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION, SharedConstants.FARE_APPLICATION);
        	aAirPriceResponceData.setDebugMessage("AirfarePriceResponseData getRepriceResponse():Unexpected Exception");
        	aAirPriceResponceData.setStatusFatalReadOnly();
            aAirPriceResponceData.setAllFromAResponseData(aSharedException.getResponseData());
        }
    	finally {
    		super.onMethodExit(aAirPriceResponceData);
		}
    		return aAirPriceResponceData;
		}

    /**
	 * This method returns the fares for the requested origin/destiantion pair
	 * and for the routes provided by the Routing engine.
	 * 
	 * @param pFareRequest
	 * @return aFareResponse
	 * @throws SharedException
	 */
    @RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public FareResponseParam getShoppingResponse(FareRequestParam pFareRequest) throws SharedException {

		// Begin collection of EJB statistics
		final Object[] methodArgs = { pFareRequest };

		FareResponseParam aFareResponse = new FareResponseParam();
		FareService aFareService = new FareService();
		FaresSearchResponseParam aFaresSearchResponse = null;
		try {
			super.onMethodEntryValidate("getShoppingResponse(FareRequest)", methodArgs);
			if(pFareRequest.getaFaresSearchRequest().getShoppingType() == 2){
				aFaresSearchResponse = aFareService.getBrandedFaresForDisplay(pFareRequest);
			}else{
				aFaresSearchResponse = aFareService.getLowestFareForAvailibility(pFareRequest);
			} 
			
			aFareResponse.setCityPairFareResponse(aFaresSearchResponse);
			aFareResponse.setStatusSuccessful();
		
		} catch (SharedException aSharedException) {
			aFareResponse.setAllFromAResponseData(aSharedException.getResponseData());
		} finally {
			super.onMethodExit(aFareResponse);
		}
		return aFareResponse;
	}

	/**
	 * This methods reads the fares search response from permanent store for the
	 * search response id, route id and solution id.
	 * 
	 * @param pFareRequest
	 * @return
	 * @throws SharedException
	 */
    @RolesAllowed({"RESERVATION_FARES_ROLE","SUPER_ROLE","AIRCORE_INTERNAL_ROLE"})
	public FareResponseParam getCompleteFareResponseForSearchId(FareRequestParam pFareRequest) throws SharedException {
		FareResponseParam aFareResponse = new FareResponseParam();
		FareService aFareService = new FareService();
		// Begin collection of EJB statistics
		final Object[] methodArgs = { pFareRequest }; 
		FareValidator.validateFareRequestForPricing(pFareRequest);
		try {
			super.onMethodEntryValidate("getCompleteFareResponseForSearchId(FareRequest)", methodArgs);

			FaresSearchResponseParam aFaresSearchResponse = aFareService.readSearchResponse(pFareRequest);
			aFareResponse.setCityPairFareResponse(aFaresSearchResponse);
			aFareResponse.setStatusSuccessful();

		} catch (SharedException aSharedException) {
			aFareResponse.setAllFromAResponseData(aSharedException.getResponseData());
		} catch (Throwable aThrowable) {
			aFareResponse.setStatusFatalReadOnly();
			aFareResponse.setReasonCode(FareReasonCodeConstants.UNEXPECTED_EXCEPTION, SharedConstants.FARE_APPLICATION);
			aFareResponse
					.setDebugMessage("AirFareEJB:getCompleteFareResponseForSearchId(FareRequest):Unexpected Exception");
			// Logs the exception
			SharedLogger.log(FareLogIdConstants.LOG_ID_1035, aThrowable, aFareResponse);
		} finally {
			super.onMethodExit(aFareResponse);
		}

		return aFareResponse;
	}

    private void rengaTest() {
    	try {
    		
    	}
    	catch(Exception e) {
    		
    	}
    	finally {
    		onMethodExit();
    		
    	}
    }
}
