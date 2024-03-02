import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import models.Cart;
import models.Cups;
import util.Connect;

public class Main extends Application{ 
	
	Scene regisScene;
	Scene loginScene;
	Scene userHome;
	Scene adminHome;
	Scene cartPage;
	Scene popUpWindow;
	//untuk userhome
	private String loggedInAs;
	private String loggedInAsName;
	TableView<Cups> cups;
	Spinner<Integer> quantity;
	Label cupName;
	Label price;
	int total;
	
	//untuk cart page
	TableView<Cart> cartTable;	
	ComboBox<String> courierComboBox;
	CheckBox insurance;
	int updatedPrice;
	int netPrice;
	int beforeInsurancePrice;
	String getSelectedCourier;
	Stage confirmStage;
	//jfxtras window transaction
	private Window transactWindow;
	
	//untuk cup management
	ObservableList<Cups> cupManagementData;
	
	private Connect connect = Connect.getInstance();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	public void alert(Alert.AlertType alertType, String title, String header, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	private EventHandler<MouseEvent> onClickCups(){
		return new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent args0) {
				// TODO Auto-generated method stub
				TableSelectionModel<Cups> tableSelectionModelCups = cups.getSelectionModel();
				tableSelectionModelCups.setSelectionMode(SelectionMode.SINGLE);				
				Cups selectedCup = tableSelectionModelCups.getSelectedItem();
				
				if (selectedCup != null) {
					
					quantity.valueProperty().addListener((obs, oldValue, newValue) -> {
						if (!oldValue.equals(newValue)) {
							cupName.setText(selectedCup.getCname());
							total = selectedCup.getCprice() * quantity.getValue();
							price.setText("Total price: " +(String.valueOf(total)));
						}
					});
					
					cupName.setText(selectedCup.getCname());
					total = selectedCup.getCprice() * quantity.getValue();
					price.setText("Total price: " +(String.valueOf(total)));
					
				}
				
			}
		};
	}
	
	public void login(Stage primaryStage) {
	    
	    BorderPane loginBp;
	    GridPane formContainer;
	    TextField nameField;
	    PasswordField passField;
	    Label nameLabel, passLabel, loginAlert, titleLabel;
	    Hyperlink registerLink;
	    Button loginButton;
	    
	    
	    ////
	    
	    loginBp = new BorderPane();
        
        loginScene = new Scene(loginBp, 1600, 900);

        formContainer = new GridPane();

        // Login
        titleLabel = new Label("Login");
        titleLabel.setFont(Font.font("Arial", 40));
        nameLabel = new Label("Username	");
        nameField = new TextField("");
        passLabel = new Label("Password	");
        passField = new PasswordField();
        loginButton = new Button("Login");
        registerLink = new Hyperlink("Don't have an account yet? Register Here!");
        loginAlert = new Label();
        
        
        
        //positioning
        
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setVgap(10);
        
        formContainer.add(nameLabel, 0, 0);
        formContainer.add(nameField, 0, 1);
        nameField.setPrefWidth(400);
        
        formContainer.add(passLabel, 0, 2);
        formContainer.add(passField, 0, 3);
        
        formContainer.add(loginAlert, 0, 4);

        loginButton.setPrefSize(100, 40);
        
        	
        loginButton.setOnAction(e -> {
        		 /* validations
               	i.	[v]Username and Password cannot be empty
        		ii.	[v]The credentials must match
                 */
                String username = nameField.getText();
                String password = passField.getText();

                if (username.isEmpty()) {
                    alert(AlertType.ERROR, "Error", "Login Error", "Please fill out your username");
                    return;
                }
                if(password.isEmpty()){
                	alert(AlertType.ERROR, "Error", "Login Error", "Please fill out your password");
                	return;
                }
                
                String queryCredicentials = String.format("SELECT * FROM msuser WHERE Username = '%s' AND UserPassword = '%s'", username, password);
                ResultSet matchCredidentials = connect.execute(queryCredicentials);
                try {
                	
					if (!matchCredidentials.next()) {
						alert(AlertType.ERROR, "Error", "Login Error", "credentials does not match");
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                try {
					String role = matchCredidentials.getString("UserRole");
					loggedInAs = matchCredidentials.getString("UserID");
					loggedInAsName = username;
					
					if (role.equals("Admin")) {
						
						
						primaryStage.setScene(adminHome);
						
					}else {
						primaryStage.setScene(userHome);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
        		adminHome(primaryStage);
        		cartPage(primaryStage);
        });
        
        registerLink.setOnAction(e -> primaryStage.setScene(regisScene));
        	
        // positioning
        VBox vBox = new VBox(30); 
        vBox.getChildren().addAll(titleLabel, formContainer,loginButton,registerLink);
        vBox.setAlignment(Pos.CENTER);
        loginBp.setCenter(vBox);
        
	}
	
	public void regis(Stage primaryStage) {
		BorderPane bp;
		GridPane formContainer;
		FlowPane genderContainer;
		
		TextField nameField;
		TextField emailField;
		PasswordField passField;
		
		Label nameLabel, emailLabel, passLabel, genderLabel, titleLabel;
		
		RadioButton maleButton,femaleButton;
		ToggleGroup genderGroup;
		
		Button registerButton;
		
		Hyperlink backToLogin;
		
		//// Declarations
		
		bp = new BorderPane();
		regisScene = new Scene(bp,1600,900);
		formContainer = new GridPane();
		genderContainer = new FlowPane();
		
		nameField = new TextField();
		emailField = new TextField();
		passField = new PasswordField();
		
		nameLabel = new Label("Name");
		emailLabel= new Label("Email");
		passLabel = new Label("Password");
		genderLabel = new Label("Gender");
		genderLabel.setFont(Font.font("Arial", 20));
		titleLabel = new Label("Register");
		titleLabel.setFont(Font.font("Arial", 40));
		
		maleButton = new RadioButton("Male");  
	    femaleButton = new RadioButton("Female");  
	    genderGroup = new ToggleGroup();
		
		registerButton = new Button("Register");
		
		backToLogin = new Hyperlink("Already have an account? Click here to login!");
		
		
		///// Validations 
		
		/*
        i.	[v] Username, Email, Password and Gender cannot be empty
        ii.	[v] Username must not exist in the database (must be unique)
        iii.[v] Email must not exist in the database (must be unique)
        iv.	[v] Email must end with @gmail.com
        v.	[v] Password must have a length of 8 � 15 characters inclusively (cannot be less than 8 or more than 15 characters)
        vi.	[v] Password must be Alphanumeric
        vii.[v] If the username contains �admin� in its username, ex: iamadmin, admin12, iadmini, then the customer will be given the role of Admin. Else, they will be given a role of User.
		*/
		
		registerButton.setOnAction(e -> {
		String name = nameField.getText();
		String email = emailField.getText().toLowerCase();
        String password = passField.getText();
        RadioButton selectedGender = (RadioButton) genderGroup.getSelectedToggle();
        String gender = selectedGender != null ? selectedGender.getText() : null;
        String role;
        
        
		// Username, Email, Password and Gender cannot be empty
        if (name.isEmpty()) {
            alert(AlertType.ERROR, "Error", "Register Error", "All fields are required.");
            return; 
        }
        if (email.isEmpty()) {
        	alert(AlertType.ERROR, "Error", "Register Error", "All fields are required.");
            return; 
		}
        if (password.isEmpty() ) {
        	alert(AlertType.ERROR, "Error", "Register Error", "All fields are required.");
            return; 
		}
        if (gender == null) {
        	alert(AlertType.ERROR, "Error", "Register Error", "All fields are required.");
        	return;
		}

        //Password must have a length of 8 � 15 characters inclusively (cannot be less than 8 or more than 15 characters)
        if (password.length()<8 || password.length()>15) {
        	alert(AlertType.ERROR, "Error", "Register Error", "Make sure your password cannot be less than 8 or more than 15 characters");
        	return;
		}
        
        //Email must end with @gmail.com
        if (!email.toLowerCase().endsWith("@gmail.com")) {
        	alert(AlertType.ERROR, "Error", "Register Error", "Make sure your email ends with '@gmail.com'");
			return;
		}
        
        //Password must be Alphanumeric
        if (!password.matches(".*\\d.*") || !password.matches(".*[a-zA-Z].*")) {
        	alert(AlertType.ERROR, "Error", "Register Error", "Make sure your password is alphanumeric");
        	return;
		}
        
        //Username must not exist in the database (must be unique)
		try {
			ResultSet duplicateNameCheck = connect.execute("SELECT * FROM msuser WHERE Username = '" + name + "'");
			if (duplicateNameCheck.next()) {
				alert(AlertType.ERROR, "Error", "Register Error", "Please choose another username");
				return;
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
		//Email must not exist in the database (must be unique)
        try {
        	ResultSet duplicateEmailCheck = connect.execute("SELECT * FROM msuser WHERE UserEmail = '" + email + "'");
			if (duplicateEmailCheck.next()) {
				alert(AlertType.ERROR, "Error", "Register Error", "Please choose another email");
				return;
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        
        
        if (name.contains("admin")) {
			// -> direct ke page admin
        	role = "Admin";
        	primaryStage.setScene(loginScene);
		}else {
			primaryStage.setScene(loginScene);
			role = "Users";
		}
        // input data ke dalam query
        
        Connect connect = Connect.getInstance();
        
        String getMaxUser = "SELECT COUNT(*) AS TotalUsers FROM msuser";
        ResultSet countUsers = connect.execute(getMaxUser);
        
        
        int totalUsers = 0;
        
        try {
			if (countUsers.next()) {
				totalUsers = countUsers.getInt("TotalUsers");
			} 
		} catch (SQLException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
        
        int newUserId = totalUsers+ 1;
   
        
        String ID = String.format("US%03d", newUserId);
        
        String insertUser = String.format("INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", ID, name, email, password, gender, role);
        System.out.println(insertUser);
        
        nameField.clear();
        emailField.clear();
        passField.clear();
        selectedGender.setSelected(false);
        
        connect.executeUpdate(insertUser);
		});
		///// positioning
		
		formContainer.setAlignment(Pos.CENTER);
		formContainer.setVgap(10);
		maleButton.setToggleGroup(genderGroup);
		femaleButton.setToggleGroup(genderGroup);
		
		genderContainer.getChildren().add(maleButton);
		genderContainer.getChildren().add(femaleButton);
		
		formContainer.add(nameLabel, 0, 0);
		formContainer.add(nameField, 0, 1);
		
		formContainer.add(passLabel, 0, 2);
		formContainer.add(passField, 0, 3);
		
		formContainer.add(emailLabel, 0, 4);
		formContainer.add(emailField, 0, 5);
		
		formContainer.add(genderLabel, 0, 6);
		formContainer.add(genderContainer, 0, 7);
		
		registerButton.setPrefSize(100, 40);
		
	    backToLogin.setOnAction(e -> primaryStage.setScene(loginScene));
	    
		
		VBox vBox = new VBox(30); 
        vBox.getChildren().addAll(titleLabel, formContainer, registerButton,backToLogin);
        vBox.setAlignment(Pos.CENTER);

        bp.setCenter(vBox);
	}
	
	public void userhome(Stage primaryStage) {

		BorderPane bp = new BorderPane();
		userHome = new Scene(bp,1600,900);
		GridPane contentContainer = new GridPane();
		//navigation bar
		
		Menu menu = new Menu("Menu");
		MenuItem home = new MenuItem("Home");
		MenuItem cart = new MenuItem("Cart");
		MenuItem out = new MenuItem("Log Out");
		MenuBar naviBar = new MenuBar(menu);
		menu.getItems().addAll(home, cart, out);
		cart.setOnAction(e -> {
			cartPage(primaryStage);
			getSelectedCourier = "";
			primaryStage.setScene(cartPage);
		});
		out.setOnAction(e -> primaryStage.setScene(loginScene));
		
		
		//Everything for table 
		String queryReadCups = "SELECT * FROM mscup";
		
		ResultSet readCups = connect.execute(queryReadCups);
		
		ArrayList<Cups> cuplisArrayList = new ArrayList<>();
		
		try {
			while (readCups.next()) {
				String cupID = readCups.getString("CupID");
				String cupName = readCups.getString("CupName");
				int cupPrice = readCups.getInt("CupPrice");
				
				Cups cupInstances = new Cups(cupID, cupName, cupPrice);
				cuplisArrayList.add(cupInstances);

			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObservableList<Cups> cupDatas = FXCollections.observableArrayList(cuplisArrayList);
		
		cups = new TableView<>(cupDatas);
		
		
		TableColumn<Cups, String> cnames = new TableColumn<>("Cup Name");
		cnames.setCellValueFactory(new PropertyValueFactory<>("cname"));
		cnames.setMinWidth(250);
		cnames.setMaxWidth(250);
		cnames.setReorderable(false);
		cnames.setResizable(false);
		
		TableColumn<Cups, Integer> cprices = new TableColumn<>("Cup Price");
		cprices.setCellValueFactory(new PropertyValueFactory<>("cprice"));
		cprices.setMinWidth(250);
		cprices.setMaxWidth(250);
		cprices.setReorderable(false);
		cprices.setResizable(false);
		
		cnames.setPrefWidth(250);
		cprices.setPrefWidth(250);
		
		cups.getColumns().add(cnames);
		cups.getColumns().add(cprices);
		
		cups.setOnMouseClicked(onClickCups()); // menjalankan event handler
		
		
		
		//home attributes
		cupName = new Label("Cup Name");
		cupName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		quantity = new Spinner<>(1, 20, 1);
		
		price = new Label("price");
		price.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		Button addCart = new Button("Add to Cart");
		
		addCart.setOnAction(e -> {
			TableSelectionModel<Cups> tableSelectionModel = cups.getSelectionModel();
			Cups selectedCup = tableSelectionModel.getSelectedItem();
			
			int amountBought = quantity.getValue();
			/*
			f.	[v]Condition 1: If the cup is not inside the cart yet, make a new cart item with the selected cup name and quantity
			g.	[v]Condition 2: If the cup is already inside the cart, add the current quantity to the old quantity inside the cart

			 */
			
			//Condition 1: If the cup is not inside the cart yet, make a new cart item with the selected cup name and quantity
			if (selectedCup == null) {
				alert(AlertType.ERROR, "Error", "Cart Error", "Please select a cup to be added");
				return;
			}
			
			selectedCup.getCid();
			
			Connect connect = Connect.getInstance();
			
			//Condition 2: If the cup is already inside the cart, add the current quantity to the old quantity inside the cart 
			String checkCartInfoQuery ="SELECT * FROM cart WHERE UserID = '" + loggedInAs + "' AND CupID = '" + selectedCup.getCid() + "'";
			ResultSet cartInfoExist = connect.execute(checkCartInfoQuery);
			
			try {
				if (cartInfoExist.next()) {
					int getExistingQuantity = cartInfoExist.getInt("Quantity");
					int setNewQuantity = getExistingQuantity + amountBought;
					String updatequantity = String.format("UPDATE cart SET Quantity = '%d' WHERE UserID = '%s' AND CupID = '%s'", setNewQuantity, loggedInAs, selectedCup.getCid());
					connect.executeUpdate(updatequantity);
					System.out.println(updatequantity);
				}else {
					String cartInsertQuery = String.format("INSERT INTO cart (UserID, CupID, Quantity) VALUES ('%s', '%s', '%d')", loggedInAs, selectedCup.getCid(), amountBought);
					connect.executeUpdate(cartInsertQuery);
					System.out.println(cartInsertQuery);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			alert(AlertType.INFORMATION, "Message", "Cart Info", "Item Succesfully added to cart!");
			
			cartPage(primaryStage);
			
		});
		
		cups.setMaxHeight(600);

		
		contentContainer.setAlignment(Pos.CENTER);
		contentContainer.setVgap(20);
		contentContainer.add(cupName, 0, 0);
		contentContainer.add(price, 0, 1);
		contentContainer.add(quantity, 0, 2);
		contentContainer.add(addCart, 0, 3);
		quantity.setMinWidth(250);
		
		
		
		bp.setTop(naviBar);
		
		
		HBox hb = new HBox(30);
		
		hb.getChildren().addAll(cups, contentContainer);
		hb.setAlignment(Pos.CENTER);
		
		bp.setCenter(hb);
		
		
	}
	
	public Window confirmWindow (Stage primaryStage) {
		
		GridPane gridButton = new GridPane();
		BorderPane bp = new BorderPane();
		transactWindow = new Window("Confirm Checkout");
		gridButton.setAlignment(Pos.CENTER);
		gridButton.setHgap(15);
		
		Label confirmLabel = new Label("Are you sure you want to purchase?");
		
		Button yes = new Button("Yes");
		
		Button no = new Button("No");
		
		gridButton.add(yes, 0, 0);
		gridButton.add(no, 1, 0);
		
		no.setOnAction(e -> {
			courierComboBox.setValue("");
			courierComboBox.setPromptText("Select a courier");
			cartPage(primaryStage);
			primaryStage.setScene(cartPage);
			confirmStage.close();
		});
		
		yes.setOnAction(e -> {
			int transactionID = 0;
			int deliveryInsuranceSelected = 0;
			String CourierID = null;
			try {
				String countTransactionIDQuery ="SELECT COUNT(*) AS TotalTransactions FROM transactionheader";
				ResultSet countTransactionID = connect.execute(countTransactionIDQuery);
				if (countTransactionID.next()) {
					int currentTransactionIDNumber = countTransactionID.getInt("TotalTransactions");
					transactionID = currentTransactionIDNumber + 1;
				}
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String transactionHeaderID = String.format("TR%03d", transactionID);
			
			String getCourierIDQuery = String.format("SELECT * FROM mscourier WHERE CourierName = '%s'", getSelectedCourier);
			ResultSet getCourierID = connect.execute(getCourierIDQuery);
			try {
				if (getCourierID.next()) {
					CourierID = getCourierID.getString("CourierID");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter dateFormatting = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String dateIntoDatabase = currentDate.format(dateFormatting);
			
			if (insurance.isSelected()) {
				deliveryInsuranceSelected = 1;
			}
			
			String insertTransactionHeader = String.format("INSERT INTO `transactionheader` (`TransactionID`, `UserID`, `CourierID`, `TransactionDate`, `UseDeliveryInsurance`) VALUES ('%s', '%s', '%s', '%s', %d)", transactionHeaderID, loggedInAs, CourierID, dateIntoDatabase, deliveryInsuranceSelected);
			
			connect.executeUpdate(insertTransactionHeader);
			
			String insertTransactionDetail = String.format("INSERT INTO `transactiondetail` (`TransactionID`, `CupID`, `Quantity`) SELECT '%s', `CupID`, `Quantity` FROM `cart` WHERE `UserID` = '%s'", transactionHeaderID, loggedInAs);
			connect.executeUpdate(insertTransactionDetail);
			
			String clearCartQuery = String.format("DELETE FROM cart WHERE UserID = '%s'", loggedInAs);
			connect.executeUpdate(clearCartQuery);
			
			courierComboBox.setValue("");
			courierComboBox.setPromptText("Select a courier");
			cartPage(primaryStage);
			primaryStage.setScene(cartPage);
		});
		
		
		VBox vbox = new VBox(40);
		vbox.setAlignment(Pos.CENTER);
		
		vbox.getChildren().addAll(confirmLabel, gridButton);
		bp.setCenter(vbox);
		
		transactWindow.getContentPane().getChildren().add(bp);
		return transactWindow;
	}
	
	public void cartPage (Stage primaryStage) {
		
		confirmStage = new Stage();
		BorderPane confirmbBp = new BorderPane();
		popUpWindow = new Scene(confirmbBp, 800, 600);
		
		BorderPane bp = new BorderPane();
		cartPage = new Scene(bp,1600,900);
		GridPane contentContainer = new GridPane();
		GridPane tableAndNameContainer = new GridPane();
		
		ArrayList<Cart> cartList = new ArrayList<>();
		
		int finalPrice = 0;
		//navigation bar
		
		Menu menu = new Menu("Menu");
		MenuItem home = new MenuItem("Home");
		MenuItem cart = new MenuItem("Cart");
		MenuItem out = new MenuItem("Log Out");
		MenuBar naviBar = new MenuBar(menu);
		menu.getItems().addAll(home, cart, out);
		home.setOnAction(e -> primaryStage.setScene(userHome));
		out.setOnAction(e -> {
			updatedPrice = 0;
			primaryStage.setScene(loginScene);
		});
		
		//table cart
		String readCartQuery = String.format("SELECT cart.UserID, cart.CupID, cart.Quantity, mscup.CupName, mscup.CupPrice FROM cart INNER JOIN mscup ON cart.CupID = mscup.CupID WHERE cart.UserID = '%s'", loggedInAs);
		ResultSet cartOfUser = connect.execute(readCartQuery);
		System.out.println(readCartQuery);
		try {
			while (cartOfUser.next()) {
			String cupId = cartOfUser.getString("CupID");
			String cupName = cartOfUser.getString("CupName");
			int cupPrice = cartOfUser.getInt("CupPrice");
			int quantity = cartOfUser.getInt("Quantity");
			int totalPrice = cartOfUser.getInt("CupPrice") * cartOfUser.getInt("Quantity");
			
			finalPrice = finalPrice + totalPrice;
			netPrice = finalPrice;
			Cart cartInstances = new Cart(cupId, cupName, cupPrice, quantity, totalPrice);
			cartList.add(cartInstances);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObservableList<Cart> cartDatas = FXCollections.observableArrayList(cartList);
		
		cartTable = new TableView<>(cartDatas);
		TableColumn<Cart, String> name = new TableColumn<>("Cup name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		name.setMinWidth(150);
		name.setReorderable(false);
		name.setResizable(false);
		
		TableColumn<Cart, Integer> cupPrice = new TableColumn<>("Price");
		cupPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		cupPrice.setMinWidth(100);
		cupPrice.setReorderable(false);
		cupPrice.setResizable(false);
		
		TableColumn<Cart, Integer> quantity = new TableColumn<>("Quantity");
		quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantity.setMinWidth(100);
		quantity.setReorderable(false);
		quantity.setResizable(false);
		
		TableColumn<Cart, Integer> total = new TableColumn<>("Total");
		total.setCellValueFactory(new PropertyValueFactory<>("total"));
		total.setMinWidth(100);
		total.setReorderable(false);
		total.setResizable(false);
		
		cartTable.getColumns().add(name);
		cartTable.getColumns().add(cupPrice);
		cartTable.getColumns().add(quantity);
		cartTable.getColumns().add(total);
		cartTable.setMinHeight(600);
		cartTable.setMaxHeight(600);
		
		
		System.out.println(finalPrice);
		
		Label cartName = new Label(loggedInAsName + "'s Cart");
		cartName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label deleteLabel = new Label("Delete Item");
		deleteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Button deleteBtn = new Button("Delete Item");
		deleteBtn.setPrefSize(100, 40);
		
		Label courierLabel = new Label("Courier");
		courierLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		courierComboBox = new ComboBox<>();
		courierComboBox.setPromptText("Select a courier");
		ObservableList<String> couriers = courierComboBox.getItems();
		couriers.addAll("JNA", "TAKA", "LoinParcel", "IRX", "JINJA");
		
		Label CourierPrice = new Label("Courier Price: ");
		CourierPrice.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		GridPane InsuranceLine = new GridPane();
		InsuranceLine.setHgap(8);
		insurance = new CheckBox();
		Label checkBoxLabel = new Label("Use Delivery Insurance");
		InsuranceLine.add(insurance, 0, 0);
		InsuranceLine.add(checkBoxLabel, 1, 0);
		
		Label totalPrice = new Label("TotalPrice: " + finalPrice);
		totalPrice.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		Button checkOut = new Button("Checkout");
		checkOut.setPrefSize(100, 40);
		
		
		deleteBtn.setOnAction(e -> {
			TableSelectionModel<Cart> tableSelectionModel = cartTable.getSelectionModel();
			Cart selectedCart = tableSelectionModel.getSelectedItem(); 
			if (selectedCart == null) {
				
				alert(AlertType.ERROR, "Error", "Deletion Error", "Please select the item to be deleted");
				return;
			}else {
				cartTable.getItems().removeAll(cartTable.getSelectionModel().getSelectedItem());
				
				String deleteItemQuery = String.format("DELETE FROM cart WHERE UserID = '%s' AND CupID = '%s'", loggedInAs, selectedCart.getId());
				connect.executeUpdate(deleteItemQuery);
				
				String reCountTotalQuery = String.format("SELECT SUM(cart.Quantity * mscup.CupPrice) AS TotalPrice FROM cart INNER JOIN mscup ON cart.CupID = mscup.CupID WHERE cart.UserID = '%s'", loggedInAs);
				ResultSet reCountTotal = connect.execute(reCountTotalQuery);
				try {
					if (reCountTotal.next()) {
						int newTotal = reCountTotal.getInt("TotalPrice");
						updatedPrice = newTotal;
						netPrice = newTotal;
						beforeInsurancePrice = newTotal;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (insurance.isSelected()) {
					updatedPrice += 2000;
				}
				
				if (courierComboBox != null) {
					getSelectedCourier = courierComboBox.getValue();
					String getCourierInfoQuery = String.format("SELECT * FROM mscourier WHERE CourierName = '%s'", getSelectedCourier);
					ResultSet CourierInfo = connect.execute(getCourierInfoQuery);
					try {
						if (CourierInfo.next()) {
							int courierPrice = CourierInfo.getInt("CourierPrice");
							updatedPrice += courierPrice;
							beforeInsurancePrice += courierPrice;
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				totalPrice.setText("TotalPrice: " + updatedPrice);
				
				alert(AlertType.INFORMATION, "Message", "Deletion Information", "Cart Deleted Succesfully");
			}
			
		});
		
		courierComboBox.setOnAction(e -> {
			getSelectedCourier = courierComboBox.getValue();
			System.out.println(getSelectedCourier);
			int newTotal = 0;
			int courierPrice = 0;
			
			try {
				String reCountTotalQuery = String.format("SELECT SUM(cart.Quantity * mscup.CupPrice) AS TotalPrice FROM cart INNER JOIN mscup ON cart.CupID = mscup.CupID WHERE cart.UserID = '%s'", loggedInAs);
				ResultSet reCountTotal = connect.execute(reCountTotalQuery);
				if (reCountTotal.next()) {
				    newTotal = reCountTotal.getInt("TotalPrice");
				}
				String getCourierInfoQuery = String.format("SELECT * FROM mscourier WHERE CourierName = '%s'", getSelectedCourier);
				ResultSet CourierInfo = connect.execute(getCourierInfoQuery);
				if (CourierInfo.next()) {
					courierPrice = CourierInfo.getInt("CourierPrice");
				}
				updatedPrice = newTotal + courierPrice;
				
				beforeInsurancePrice = updatedPrice;
				
				if (insurance.isSelected()) {
					updatedPrice += 2000;
					totalPrice.setText("TotalPrice: " + updatedPrice);
				}else {
					totalPrice.setText("TotalPrice: " + updatedPrice);
				}
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		});
		
		insurance.setOnAction(e -> {
			String reCountTotalQuery = String.format("SELECT SUM(cart.Quantity * mscup.CupPrice) AS TotalPrice FROM cart INNER JOIN mscup ON cart.CupID = mscup.CupID WHERE cart.UserID = '%s'", loggedInAs);
			ResultSet reCountTotal = connect.execute(reCountTotalQuery);
			
			
			if (updatedPrice == 0) {
				try {
					if (reCountTotal.next()) {
					    int newTotal = reCountTotal.getInt("TotalPrice");
					    if (insurance.isSelected()) {
							int newTotalWithInsurance = newTotal + 2000;
							totalPrice.setText("TotalPrice: " + newTotalWithInsurance);
						}else {
							totalPrice.setText("TotalPrice: " + newTotal);
						}
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else {
				if (insurance.isSelected()) {
					int afterInsurance = beforeInsurancePrice + 2000;
					totalPrice.setText("TotalPrice: " + afterInsurance);
				}else if(!insurance.isSelected()){
					
					totalPrice.setText("TotalPrice: " + beforeInsurancePrice);
				}
			}
			
		});
		
		checkOut.setOnAction(e -> {
			if (getSelectedCourier.isEmpty()) {
				alert(AlertType.ERROR, "Error", "Courier Error", "Select a courier to proceed to checkout");
				return;
			}
			if (cartDatas.isEmpty()) {
				alert(AlertType.ERROR, "Error", "Checkout Error", "No item to check out");
				return;
			}
			
			int checkoutPrice = netPrice;
			System.out.println("netprice: " + netPrice);
			
			if (insurance.isSelected()) {
				int insuranceFee = checkoutPrice + 2000;
				checkoutPrice = insuranceFee;
			}
			
			
			
			
			if (courierComboBox != null) {
				getSelectedCourier = courierComboBox.getValue();
				
				String getCourierInfoQuery = String.format("SELECT * FROM mscourier WHERE CourierName = '%s'", getSelectedCourier);
				ResultSet CourierInfo = connect.execute(getCourierInfoQuery);
				try {
					if (CourierInfo.next()) {
						int courierPrice = CourierInfo.getInt("CourierPrice");
						checkoutPrice += courierPrice;

						Window transactWindow = confirmWindow(primaryStage);
						
						confirmbBp.setCenter(transactWindow);
						
						confirmStage.setScene(popUpWindow);
						
						confirmStage.showAndWait();
//						
//						bp.setTop(null);
//						bp.setCenter(transactWindow);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			System.out.println("Checkout Price: "+ checkoutPrice);
		});
		tableAndNameContainer.setAlignment(Pos.CENTER);
		tableAndNameContainer.setVgap(10);
		tableAndNameContainer.add(cartName, 0, 0);
		tableAndNameContainer.add(cartTable, 0, 1);
		
		contentContainer.setAlignment(Pos.CENTER);
		contentContainer.setVgap(15);
		
		contentContainer.add(deleteLabel, 0, 0);
		contentContainer.add(deleteBtn, 0, 1);
		contentContainer.add(courierLabel, 0, 2);
		contentContainer.add(courierComboBox, 0, 3);
		contentContainer.add(CourierPrice, 0, 4);
		contentContainer.add(InsuranceLine, 0, 5);
		contentContainer.add(totalPrice, 0, 6);
		contentContainer.add(checkOut, 0, 7);
		
		HBox hb = new HBox(30);
		hb.getChildren().addAll(tableAndNameContainer, contentContainer);
		hb.setAlignment(Pos.CENTER);
		
		bp.setTop(naviBar);
		bp.setCenter(hb);
	}	
		
 	public void adminHome (Stage primaryStage) {
			
		BorderPane bp = new BorderPane();
		adminHome = new Scene(bp,1600,900);
		GridPane contentContainer = new GridPane();
		GridPane titleAndTableContainer = new GridPane();
			
			
		// navi bar
		Menu menu = new Menu("Menu");
		MenuItem cupManagement = new MenuItem("Cup Management");
		MenuItem logOut = new MenuItem("Log Out");
		MenuBar  naviBar = new MenuBar(menu);
		menu.getItems().addAll(cupManagement, logOut);
		
		logOut.setOnAction(e -> primaryStage.setScene(loginScene));
		
		Label titleCupManagement = new Label("Cup Management");
		titleCupManagement.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		Label nameLabel = new Label("Cup Name");
		nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		TextField nameField = new TextField();
			
		Label priceLabel = new Label("Cup Price");
		priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		TextField priceField = new TextField();
		priceField.setPrefWidth(300);
		
		Button add = new Button("Add Cup");
		add.setPrefSize(200, 50);
		
		Button upd = new Button("Update Cup");
		upd.setPrefSize(200, 50);
		Button rmv = new Button("Remove Cup");
		rmv.setPrefSize(200, 50);
			
			
		//Everything for table
				
		ArrayList<Cups> cupManagementArray = new ArrayList<>();
			
		String getCupListQuery = "SELECT * FROM mscup";
		ResultSet getCupList = connect.execute(getCupListQuery);
				
		try {
			while (getCupList.next()) {
				String cupId = getCupList.getString("CupID");
				String cupName = getCupList.getString("CupName");
				int cupPrice = getCupList.getInt("CupPrice");
						
				Cups cupsInstancesCups = new Cups(cupId, cupName, cupPrice);
				cupManagementArray.add(cupsInstancesCups);
						
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		cupManagementData = FXCollections.observableArrayList(cupManagementArray);
				
		TableView<Cups> cups = new TableView<>(cupManagementData);
				
		TableColumn<Cups, String> cnames = new TableColumn<>("Cup Name");
		cnames.setCellValueFactory(new PropertyValueFactory<>("cname"));
		cnames.setResizable(false);
		cnames.setReorderable(false);
		cnames.setMinWidth(250);
				
		TableColumn<Cups, Integer> cprices = new TableColumn<>("Cup Price");
		cprices.setCellValueFactory(new PropertyValueFactory<>("cprice"));
		cprices.setResizable(false);
		cprices.setReorderable(false);
		cprices.setMinWidth(250);
				
		cups.getColumns().add(cnames);
		cups.getColumns().add(cprices);
				
		cups.setMinHeight(600);
		cups.setMaxHeight(600);

			
		//button functionality
		
		/*
		VALIDATION 
		1. [v]Validate that the input fields must not be empty
		2. [v]cup�s name is unique
		3. [v]price range is 5000 � 1000000 inclusively
		
		 */
		
		add.setOnAction(e -> {
			String newCupName = nameField.getText();
			String getCupPriceFromField = priceField.getText();
			int newCupPrice;
			String newCupID;
			
			// Validate that the input fields must not be empty
			if (newCupName.isEmpty()) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "name cannot be empty");
				return;
			}
			
			if (getCupPriceFromField.isEmpty()) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "price cannot be empty");
				return;
			}
			
			if (!getCupPriceFromField.matches("\\d*")) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "Price must be in integer");
				return;
			}else {
				newCupPrice = Integer.parseInt(getCupPriceFromField);
			}
			
			String checkUniqueCupNameQuery = String.format("SELECT * FROM mscup WHERE CupName = '%s'", newCupName);
			ResultSet checkUniqueCupName = connect.execute(checkUniqueCupNameQuery);
			
			try {
				if (checkUniqueCupName.next()) {
					alert(AlertType.ERROR, "Error", "Cup Management Error", "Name must be unique");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (newCupPrice < 5000 || newCupPrice > 1000000) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "price must range from 5000 - 1000000");
				return;
			}
			
			String CheckCupIDQuery = "SELECT COUNT(*) AS TotalCup FROM mscup";
			ResultSet getTotalID = connect.execute(CheckCupIDQuery);
			
			int numberCupID = 0;
			
			try {
				if (getTotalID.next()) {
					numberCupID = getTotalID.getInt("TotalCup");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			while (true) { // menamabah nomor ID jika ada duplicate ID
				numberCupID += 1;
				
				newCupID = String.format("CU%03d", numberCupID);
				
				String CheckDuplicateIDQuery = String.format("SELECT * FROM mscup WHERE CupID = '%s'", newCupID);
				ResultSet CheckDuplicateID = connect.execute(CheckDuplicateIDQuery);
				
				try {
					if (!CheckDuplicateID.next()) {
						break;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			String insertCupDatabaseQuery = String.format("INSERT INTO mscup (CupID, CupName, CupPrice) VALUES ('%s', '%s', '%d')", newCupID, newCupName, newCupPrice);
			
			System.out.println(insertCupDatabaseQuery);

			connect.executeUpdate(insertCupDatabaseQuery);
			
			Cups addCup = new Cups(newCupID, newCupName, newCupPrice);
			
			cupManagementArray.add(addCup);
			
			cups.setItems(FXCollections.observableArrayList(cupManagementArray));
			
			nameField.clear();
			priceField.clear();
			
		});
		
		upd.setOnAction(e -> {
			String newCupName = nameField.getText();
			String getCupPriceFromField = priceField.getText();
			int newCupPrice;
			
			TableSelectionModel<Cups> tableSelectionModel = cups.getSelectionModel();
			Cups selectedCup = tableSelectionModel.getSelectedItem();
			
			if (selectedCup == null) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "Please select a cup to update");
				return;
			}else {

				// Validate that the input fields must not be empty
				if (newCupName.isEmpty()) {
					alert(AlertType.ERROR, "Error", "Cup Management Error", "name cannot be empty");
					return;
				}
				
				if (getCupPriceFromField.isEmpty()) {
					alert(AlertType.ERROR, "Error", "Cup Management Error", "price cannot be empty");
					return;
				}
				
				if (!getCupPriceFromField.matches("\\d*")) {
					alert(AlertType.ERROR, "Error", "Cup Management Error", "Price must be in integer");
					return;
				}else {
					newCupPrice = Integer.parseInt(getCupPriceFromField);
				}
				
				String checkUniqueCupNameQuery = String.format("SELECT * FROM mscup WHERE CupName = '%s'", newCupName);
				ResultSet checkUniqueCupName = connect.execute(checkUniqueCupNameQuery);
				
				try {
					if (checkUniqueCupName.next()) {
						alert(AlertType.ERROR, "Error", "Cup Management Error", "Name must be unique");
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (newCupPrice < 5000 || newCupPrice > 1000000) {
					alert(AlertType.ERROR, "Error", "Cup Management Error", "price must range from 5000 - 1000000");
					return;
				}
				
				String newCupID = selectedCup.getCid();
				
				String updateCupDatabaseQuery = String.format("UPDATE mscup SET CupName = '%s', CupPrice = '%d' WHERE CupID = '%s'", newCupName, newCupPrice, newCupID);
				
				System.out.println(updateCupDatabaseQuery);
				
				connect.executeUpdate(updateCupDatabaseQuery);
				
				selectedCup.setCname(newCupName);
				selectedCup.setCprice(newCupPrice);
				
				cups.refresh();
				
				nameField.clear();
				priceField.clear();
				
			}
		});
		
		rmv.setOnAction(e -> {
			TableSelectionModel<Cups> tableSelectionModel = cups.getSelectionModel();
			Cups selectedCup = tableSelectionModel.getSelectedItem();
			
			if (selectedCup == null) {
				alert(AlertType.ERROR, "Error", "Cup Management Error", "Please select a cup to delete");
				return;
			}else {
				cups.getItems().removeAll(selectedCup);
				cupManagementArray.remove(selectedCup);
				String removeCupObjectQuery = String.format("DELETE FROM mscup WHERE CupID = '%s'", selectedCup.getCid());
				connect.executeUpdate(removeCupObjectQuery);
			}
			
			for (Cups cups2 : cupManagementArray) {
				System.out.println(cups2.getCid());
				System.out.println(cups2.getCname());
				System.out.println(cups2.getCprice());
			}
			
		});
		
		
		
		//positioning
		titleAndTableContainer.setAlignment(Pos.CENTER);
		titleAndTableContainer.setVgap(10);
		titleAndTableContainer.add(titleCupManagement, 0, 0);
		titleAndTableContainer.add(cups, 0, 1);
		
		contentContainer.setAlignment(Pos.CENTER);
		contentContainer.setVgap(15);
		contentContainer.add(nameLabel, 0, 0);
		contentContainer.add(nameField, 0, 1);
		contentContainer.add(priceLabel, 0, 2);
		contentContainer.add(priceField, 0, 3);
		contentContainer.add(add, 0, 4);
		contentContainer.add(upd, 0, 5);
		contentContainer.add(rmv, 0, 6);
			
		bp.setTop(naviBar);
		
		HBox hb = new HBox(30);
		hb.getChildren().addAll(titleAndTableContainer, contentContainer);
		hb.setAlignment(Pos.CENTER);
		
		bp.setCenter(hb);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		regis(primaryStage);
		login(primaryStage);
		userhome(primaryStage);
		adminHome(primaryStage);
		cartPage(primaryStage);
		
		primaryStage.setTitle("cangkIR");
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
}