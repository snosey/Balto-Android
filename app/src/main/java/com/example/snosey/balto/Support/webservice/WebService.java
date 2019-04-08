package com.example.snosey.balto.Support.webservice;

/**
 * Created by pc on 8/26/2017.
 */

public class WebService {

    public static String homeVisit = "1";

    public static String onlineConsult = "2";

    public static class VideoCall {
        public static String apiSecret = "0Ydn1IuRXm40R4uboqnnmpNdjKjw2lSV";
        public static String apiSID = "SK6f1cb9c5f7b0d20e5e8c13dd30694ce3";
        public static String accountSID = "AC846e17fc86ed5f24590d6ade5ac6eb05";
        public static String AUTHTOKEN = "bb483c0e2b67f6fa9ba7034216af8070";
        public static String roomName = "Balto";
        public static String newRoomApi = "http://haseboty.com/doctor/public/api/createRoom?";
        public static String id_user = "id_user";
        public static String id_booking = "id_booking";
        public static String video_token = "video_token";
    }

    public static class Notification {
        public static class Types {
            public static String bookingRequest = "bookingRequest";
            public static String acceptRequestHomeVisit = "acceptRequestHomeVisit";
            public static String alarm = "alarm";
            public static String bookingRequestOnline = "bookingRequestOnline";
            public static String newReservation = "New Reservation";
            public static String video_call = "video_call";
            public static String newMsg = "newMsg";
        }

        public static String reg_id = "reg_id[]";
        public static String message = "message";
        public static String kind = "kind";
        public static String data = "data";
        public static String title = "title";
        public static String FIREBASE_API_KEY = "AAAAg9tN8oI:APA91bE_9tV2K5V98_ZcimKSJ0Uk3EQ_qLIznI3SH7IFOjgjWCRxEdkwf-zTfIHFaJ1gN8z56GN3gghaxqR_WdlSBZASqwzjJdEGPqD2ewz9iIlkK387OWzcz20fpot1L48S6l1RTeak";
        public static String paramter = "FIREBASE_API_KEY=" + FIREBASE_API_KEY + "&";
        public static String notificationApi = "http://haseboty.com/sendNotification/sendNotification.php?" + paramter;
    }

    public static class Booking {
        public static String deleteBookingApi = "http://haseboty.com/doctor/public/api/deleteBooking?";
        public static String reservationsApi = "http://haseboty.com/doctor/public/api/reservationsData?";
        public static String doctor = "doctor";
        public static String client = "client";
        public static String fcm_token_doctor = "fcm_token_doctor";
        public static String fcm_token_client = "fcm_token_client";
        public static String video_token = "video_token";
        public static String id_client = "id_client";
        public static String lang = "lang";
        public static String type = "type";
        public static String state = "state";
        public static String id_user = "id_user";
        public static String addPaymentApi = "http://haseboty.com/doctor/public/api/addPayment?";
        public static String total_money = "total_money";
        public static String cash = "1";
        public static String credit = "2";
        public static String id_booking = "id_booking";
        public static String addBookingApi = "http://haseboty.com/doctor/public/api/addBooking?";
        public static String id_sub = "id_sub";
        public static String total_price = "total_price";
        public static String duration = "duration";
        public static String id_coupon_client = "id_coupon_client";
        public static String client_latitude = "client_latitude";
        public static String client_longitude = "client_longitude";
        public static String client_address = "client_address";
        public static String receive_day = "receive_day";
        public static String receive_month = "receive_month";
        public static String receive_year = "receive_year";
        public static String receive_hour = "receive_hour";
        public static String id_doctor_kind = "id_doctor_kind";
        public static String receive_minutes = "receive_minutes";
        public static String updateBookingApi = "http://haseboty.com/doctor/public/api/updateBooking?";
        public static String id = "id";
        public static String id_doctor = "id_doctor";
        public static String id_state = "id_state";
        public static String medication = "medication";
        public static String diagnosis = "diagnosis";
        public static String id_coupon_doctor = "id_coupon_doctor";
        public static String getBookDataApi = "http://haseboty.com/doctor/public/api/bookingData?";
        public static String rateBookingApi = "http://haseboty.com/doctor/public/api/rateBooking?";
        public static String editRateBookingApi = "http://haseboty.com/doctor/public/api/editRateBooking?";

        public static String rate = "rate";
        public static String coming = "coming";
        public static String past = "past";
        public static String review = "review";

        public static String bookingStateSearch = "1";
        public static String bookingStateProcessing = "2";
        public static String bookingStateStart = "3";
        public static String bookingStateWorking = "4";
        public static String bookingStateDone = "5";
        public static String bookingStateCancel = "6";
        public static String bookingStatePatientCancel = "7";
        public static String bookingStateDoctorCancel = "8";
        public static String bookingStateTimeout = "9";

        public static String id_payment_way = "id_payment_way";
        public static String subCategoryName = "subCategoryName";
        public static String doctorKindName = "doctorKindName";

        public static String firstName = "firstName";
        public static String totalRate = "totalRate";
        public static String fcm_token = "fcm_token";
        public static String homeVisit = "Home Visit";
        public static String phone = "phone";
        public static String client_token = "client_token";
        public static String lastName = "lastName";
        public static String getUserLocationApi = "http://haseboty.com/doctor/public/api/doctorLocation?";
        public static String getUserLocation = "getUserLocation";
        public static String deleteBooking = "deleteBooking";
        public static String rateId = "rateId";
        public static String deleteRateApi = "http://haseboty.com/doctor/public/api/deleteRateBooking?";
        public static String wallet = "6";
        public static String wallet_id = "wallet_id";
        public static String id_rate = "id_rate";
    }

    public static class HomeVisit {
        public static String id_user = "id_user";
        public static String id_main = "id_main";
        public static String longitude = "longitude";
        public static String latitude = "latitude";
        public static String nearestDoctorApi = "http://haseboty.com/doctor/public/api/nearestDoctor?";
        public static String MainCatApi = "http://haseboty.com/doctor/public/api/mainCategory?";
        public static String SubCategoryApi = "http://haseboty.com/doctor/public/api/subCategoryByMain?";
        public static String id_sub = "id_sub";
        public static String id_gender = "id_gender";
        public static String duration = "duration";
        public static String promoCode = "promoCode";
        public static String totalPrice = "totalPrice";
        public static String id_doctor_kind = "id_doctor_kind";
        public static String homeVisit = "1";
        public static String distance = "distance";
        public static String SubCategory = "SubCategory";
    }

    public static class Slider {
        public static String userRateApi = "http://haseboty.com/doctor/public/api/totalRate?";
        public static String id_user = "id_user";
        public static String allUserRateApi = "http://haseboty.com/doctor/public/api/userRate?";
        public static String total_rate = "total_rate";
        public static String onlineApi = "http://haseboty.com/doctor/public/api/makeDoctorOnline?";
        public static String online = "online";
        public static String on = "1";
        public static String off = "0";
    }

    public static class SMS {
        public static String url = "https://smsmisr.com/api/webapi/?";
        public static String Username = "Username";
        public static String usernameBalto = "BDRYDK6Z";
        public static String passwordBalto = "7E7XCU";
        public static String password = "password";
        public static String language = "language";
        public static String sender = "sender";
        public static String senderElbalto = "ElBalto";
        public static String message = "message";
        public static String Mobile = "Mobile";
    }

    public static class Image {
        public static final String imageLink = "doctor/public/images/";
        public static String baseUrl = "http://haseboty.com/";
        public static String fullPathImage = baseUrl + imageLink;
        public static final String fileName = "uploadImage.php";
        public static final String apiConfig = imageLink + fileName;

    }

    public static class Login {
        public static String loginApi = "http://haseboty.com/doctor/public/api/login?";
        public static String email = "email";
        public static String forgetPasswordApi = "http://haseboty.com/doctor/public/api/forgetPassword?";
        public static String password = "password";
        public static String id_provider = "id_provider";
        public static String fcm_token = "fcm_token";
        public static String card_type = "card_type";
        public static String card_number = "card_number";
        public static String payment_token = "payment_token";
    }

    public static class SignUp {
        public static String id_sub = "id_sub[]";

        public static String fcm_token = "fcm_token";
        public static String getDataApi = "http://haseboty.com/doctor/public/api/dataSingup?";
        public static String getDataApiAr = "type=ar";
        public static String getDataApiEn = "type=en";

        public static String checkEmailApi = "http://haseboty.com/doctor/public/api/userCheck?";
        public static String email = "email";

        public static String signUpRequestApi = "http://haseboty.com/doctor/public/api/singUp?";
        public static String password = "password";
        public static String id_gender = "id_gender";
        public static String first_name_en = "first_name_en";
        public static String last_name_en = "last_name_en";
        public static String first_name_ar = "first_name_ar";
        public static String last_name_ar = "last_name_ar";
        public static String phone = "phone";
        public static String typeDoctor = "doctor";
        public static String typeClient = "client";
        public static String type = "type";
        public static String image = "image";
        public static String id_government = "id_government";
        public static String id_city = "id_city[]";
        public static String id_doctor_kind = "id_doctor_kind";
        public static String provider_kind = "provider_kind";
        public static String id_language = "id_language";

        public static String providerCheckApi = "http://haseboty.com/doctor/public/api/ProviderCheck?";
        public static String id_provider = "id_provider";

    }

    public static class PromoCode {
        public static String promoCodeCheckApi = "http://haseboty.com/doctor/public/api/checkCouponNew?";
        public static String promoCodeCheckApiNew = "http://haseboty.com/doctor/public/api/checkCouponNew?";
        public static String code = "code";
        public static String newPromoCodeApi = "http://haseboty.com/doctor/public/api/Add_coupon?";
        public static String increasePromoCodeUsageApi = "http://haseboty.com/doctor/public/api/addCouponCurrent?";
        public static String added_by = "add_by";
        public static String id_coupon = "id_coupon";
        public static String id_user = "id_user";
        public static String getPromoCodeUserApi = "http://haseboty.com/doctor/public/api/CouponsByUser?";
        public static String addPromoCodeToUserApi = "http://haseboty.com/doctor/public/api/CouponUser?";
        public static String discount = "discount";
        public static String id = "id";
    }

    public static class Location {
        public static String updateLocationApi = "http://haseboty.com/doctor/public/api/updateDoctorLocation?";
        public static String id = "id";
        public static String longitude = "longitude";
        public static String latitude = "latitude";
    }

    public static class Schedule {
        public static String getScheduleApi = "http://haseboty.com/doctor/public/api/getSchedule?";
        public static String getScheduleDataApi = "http://haseboty.com/doctor/public/api/getScheduleByDate?";
        public static String updateScheduleApi = "http://haseboty.com/doctor/public/api/updateSchedule?";
        public static String deleteScheduleApi = "http://haseboty.com/doctor/public/api/deleteSchedule?";
        public static String addScheduleApi = "http://haseboty.com/doctor/public/api/addSchedule?";
        public static String day = "day";
        public static String month = "month";
        public static String year = "year";
        public static String from_hour = "from_hour";
        public static String to_hour = "to_hour";
        public static String from_minutes = "from_minutes";
        public static String to_minutes = "to_minutes";
        public static String id_user = "id_user";
        public static String type = "type";
        public static String id = "id";
    }

    public static class OnlineConsult {
        public static String getFilterDataApi = "http://haseboty.com/doctor/public/api/doctorFiltterData?";
        public static String getDoctorsApi = "http://haseboty.com/doctor/public/api/fillterOnlineDoctor?";
        public static String getAvailableDoctorToChatApi = "http://haseboty.com/doctor/public/api/getAvailableDoctorToChat?";
        public static String doctorBookingTimeApi = "http://haseboty.com/doctor/public/api/doctorBookingTime?";
        public static String id_sub = "id_sub";
        public static String day = "day";
        public static String id_doctor = "id_doctor";
        public static String id_gender = "id_gender";
        public static String id_language = "id_language";
        public static String name = "name";
        public static String month = "month";
        public static String year = "year";
        public static String type = "type";

        public static String lastName = "lastName";
        public static String firstName = "firstName";
        public static String subName = "subName";
        public static String price = "price";
        public static String createChatApi = "http://haseboty.com/doctor/public/api/createChat?";
        public static String getMessagesApi = "http://haseboty.com/doctor/public/api/getMessages?";
        public static String getChatsApi = "http://haseboty.com/doctor/public/api/getChats?";
        public static String updateChatApi = "http://haseboty.com/doctor/public/api/updateChat?";
        public static String sendMessagesApi = "http://haseboty.com/doctor/public/api/createMessage";

    }

    public static class Credit {
        public static String askForAuthApi = "https://accept.paymobsolutions.com/api/auth/tokens";
        public static String orderApi = "https://accept.paymobsolutions.com/api/ecommerce/orders";
        public static String paymentKeyRequestApi = "https://accept.paymobsolutions.com/api/acceptance/payment_keys";
        public static String cardHolderName = "cardHolderName";
        public static String cardNumber = "cardNumber";
        public static String cvv = "cvv";
        public static String expiry = "expiry";
        public static String paymentWay = "paymentWay";
        public static String credit = "credit";
        public static String cash = "cash";
        public static String payNowApi = "https://accept.paymobsolutions.com/api/acceptance/payments/pay";
    }

    public static class Setting {

        public static String reviewApi = "http://haseboty.com/doctor/public/api/userRateData?";
        public static String updateUserApi = "http://haseboty.com/doctor/public/api/updateUser?";
        public static String getDoctorApi = "http://haseboty.com/doctor/public/api/doctorData?";
        public static String getClientApi = "http://haseboty.com/doctor/public/api/clientData?";
        public static String subName = "subName";
        public static String mainName = "mainName";
        public static String genderName = "genderName";
        public static String deleteCerApi = "http://haseboty.com/doctor/public/api/deleteDoctorDocuments?";
        public static String addCerApi = "http://haseboty.com/doctor/public/api/addDoctorDocuments?";
        public static String id = "id";
        public static String default_location = "default_location";
    }

    public static class Payment {
        public static String updateWalletStateApi = "http://haseboty.com/doctor/public/api/updateWalletState?";
        public static String selectUserTransactionApi = "http://haseboty.com/doctor/public/api/selectUserTransaction?";
        public static String addTransactionApi = "http://haseboty.com/doctor/public/api/addTransaction?";
        public static String onlinePaymentApi = "http://haseboty.com/doctor/public/api/onlinePaymentGuide";
        public static String walletPaymentApi = "http://haseboty.com/doctor/public/api/walletPaymentGuide";
        public static String amanPaymentApi = "http://haseboty.com/doctor/public/api/amanPaymentGuide";

        public static String user_id = "user_id";
        public static String amount = "amount";
        public static String online = "online";
        public static String state = "state";
        public static String paymentWay = "paymentWay";
        public static String id = "id";
        public static String data = "data";
        public static String status = "status";


        public static String allPaymentTodoctor = "allPaymentTodoctor";
        public static String allPaymentToadmin = "allPaymentToadmin";
        public static String doctorPercentageMoneyApi = "http://haseboty.com/doctor/public/api/doctorPercentageMoney?";
        public static String getDoctorPayment = "http://haseboty.com/doctor/public/api/allPaymentsDoctor?";
        public static String addPayment = "http://haseboty.com/doctor/public/api/addPayment?";
        public static String online_percentage = "online_percentage";
        public static String home_percentage = "home_percentage";
        public static String type = "type";
        public static String total_money = "total_money";
        public static String admin_money = "admin_money";
        public static String payMob_id = "payMob_id";
        public static String id_user = "id_user";
        public static String depet = "depet";
        public static String doctor_money = "doctor_money";
        public static String id_payment_way = "id_payment_way";
        public static String id_booking = "id_booking";
        public static String doctorPercentageMoney = "doctorPercentageMoney";
        public static String payLive1 = "725";
        public static String payLive2 = "1428";

        // public static String payLive1 = "725";
        // public static String payLive2 = "1428";

        // public static String payTest1 = "925";
        //  public static String payTest2 = "241";

        public static String AmanPayTest1 = "2270";
        public static String AmanPayTest2 = "2270";
        public static String created_at = "updated_at";
        public static String error = "error";
        public static String paymentName = "paymentName";
        public static String total_amount = "total_amount";
        public static String lng = "lang";
        public static String total_outstanding = "total_outstanding";
        public static String total_pending = "total_pending";
        public static String phone = "phone";
        public static String outstanding = "4";
        public static String way = "way";
        public static String user = "user";
        public static String total_credit = "total_credit";
        public static String accept = "2";
        public static String done = "5";
        public static String refunded = "6";
        public static String direct = "direct";
    }

    public static class MapStyle {
        public static String mapStyle = "[\n" +
                "    {\n" +
                "        \"featureType\": \"all\",\n" +
                "        \"elementType\": \"labels.text.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#7c93a3\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"lightness\": \"-10\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.country\",\n" +
                "        \"elementType\": \"geometry\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.country\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a0a4a5\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"administrative.province\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#62838e\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"landscape\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#dde3e3\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"landscape.man_made\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#3f4a51\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.30\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"simplified\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.attraction\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.business\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.government\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.park\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.place_of_worship\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.school\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"poi.sports_complex\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"saturation\": \"-100\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#bbcacf\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"lightness\": \"0\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"color\": \"#bbcacf\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.50\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"labels\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway\",\n" +
                "        \"elementType\": \"labels.text\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"on\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway.controlled_access\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#ffffff\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.highway.controlled_access\",\n" +
                "        \"elementType\": \"geometry.stroke\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a9b4b8\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"road.arterial\",\n" +
                "        \"elementType\": \"labels.icon\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"invert_lightness\": true\n" +
                "            },\n" +
                "            {\n" +
                "                \"saturation\": \"-7\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"lightness\": \"3\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"gamma\": \"1.80\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"weight\": \"0.01\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"transit\",\n" +
                "        \"elementType\": \"all\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"visibility\": \"off\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"featureType\": \"water\",\n" +
                "        \"elementType\": \"geometry.fill\",\n" +
                "        \"stylers\": [\n" +
                "            {\n" +
                "                \"color\": \"#a3c7df\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";

    }

}
