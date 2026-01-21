module com.chatgrupalsockets {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.chatgrupalsockets to javafx.fxml;
    exports com.chatgrupalsockets;
}