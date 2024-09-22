package weather1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.awt.color.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class weather_1 extends JFrame {


	// API keys and their names

	// visit whenever you use API related stuff
    private static final String API_KEY = "5b88dcc18a8042dba2c202637242109"; //this may change after 4th october 2024
    //for current weather
    private static final String CURRENT_WEATHER_URL = "https://api.weatherapi.com/v1/current.json?key=";
    //for 5 day forecast
    private static final String FORECAST_URL = "https://api.weatherapi.com/v1/forecast.json?key=";
    //keep it ..lets see if this works or not.
    private static final String IP_LOOKUP_URL = "https://api.weatherapi.com/v1/ip.json?key=";


    //for panel

    private JLabel currentWeatherLabel;
    private JPanel forecastPanel;
    private JPanel infoPanel;
    private JLabel infoLabel;
     //constructor where i can give everything
    //i guess all.

    public weather_1()
    {

    	//title
        setTitle("Arshad weather App");

        //setting size
        setSize(400, 500);

        //exit on close
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //layout.......i like border layout
        setLayout(new BorderLayout());

        // Create top panel for user input and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        // button and text on it..
        JTextField cityInput = new JTextField(15);
        JButton getWeatherButton = new JButton("get info my dear");

        //will see if the time permits..keep it as it is for now.
        JButton useLocationButton = new JButton("use my location");


        //adding labels, text field and button
        topPanel.add(new JLabel("City:"));
        topPanel.add(cityInput);
        topPanel.add(getWeatherButton);
        //will see after sometime
        topPanel.add(useLocationButton);

        add(topPanel, BorderLayout.NORTH);

        
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));  // Add a small border
        infoPanel.setVisible(false);  // Initially invisible

        infoLabel = new JLabel("<html><b>Weather App Info</b><br>This app retrieves weather information for cities based on the OpenWeatherMap API.<br>[Optional: You can add more details here]</html>");
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        
        
        // Creating label for current weather
        currentWeatherLabel = new JLabel("i got this info about the city you entered", SwingConstants.CENTER);
        currentWeatherLabel.setPreferredSize(new Dimension(400, 100));
        add(currentWeatherLabel, BorderLayout.CENTER);

        // Creating panel for the result to display
        forecastPanel = new JPanel();
        forecastPanel.setLayout(new GridLayout(5, 1));
        add(forecastPanel, BorderLayout.SOUTH);

        // action listeners if you want to work constantly..
        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityInput.getText();
                displayWeatherForCity(city);
            }
        });

        //addInfoIcon(headerPanel);
        //not sure why it is not working
        useLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = getUserLocation();
                displayWeatherForCity(city);
            }
        });
    }

    // show todays weather and 5 day forecast for  given city
    private void displayWeatherForCity(String city) {
        try {
            String weatherResponse = getWeatherData(city);
            String forecastResponse = getFiveDayForecast(city);

            // what? just update the current weather label
            displayCurrentWeather(weatherResponse);

            // its simple..Update the forecast panel
            displayForecast(forecastResponse);

        } catch (Exception e) {
            currentWeatherLabel.setText("Error fetching weather data.");
            e.printStackTrace();
        }
    }

    // Get weather data for a city
    private String getWeatherData(String city) throws Exception {
        String url = CURRENT_WEATHER_URL + API_KEY + "&q=" + city;
        return sendGetRequest(url);
    }

    // Get 5-day forecast data for a city
    private String getFiveDayForecast(String city) throws Exception {
        String url = FORECAST_URL + API_KEY + "&q=" + city + "&days=5";//using 5 because..if you want more days..just come here and change the no. of days
        return sendGetRequest(url);
    }

    // Get user's location based on their IP address
    private String getUserLocation() {
    	//using exception handling because i am not sure if this works or not.
        try
        {
            String url = IP_LOOKUP_URL + API_KEY + "&q=auto:ip";// will see how it works.
            String response = sendGetRequest(url);
            JsonObject locationJson = JsonParser.parseString(response).getAsJsonObject();
            return locationJson.get("city").getAsString();
        }
        catch (Exception e1)
        {
            currentWeatherLabel.setText("Error fetching location.");
            return null;
        }
    }

    //yay..it worked...

    // Sending GET request to URL given and return response as string
    private static String sendGetRequest(String url) throws Exception// not sure which error i will it throw
    {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();//returning the response we got using our URL and API
    }

    // show weather details
    private void displayCurrentWeather(String response) {

    	//using json
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject currentWeather = jsonObject.getAsJsonObject("current");
        JsonObject location = jsonObject.getAsJsonObject("location");

        //
        String cityName = location.get("name").getAsString();//for name of the city entered
        double temperature = currentWeather.get("temp_c").getAsDouble();// taking celcius temperature...you can change to fahrenheit by keeping temp_f
        String condition = currentWeather.getAsJsonObject("condition").get("text").getAsString();

        String iconUrl = "https:" + currentWeather.getAsJsonObject("condition").get("icon").getAsString();

        currentWeatherLabel.setText("<html>Weather in " + cityName + ":<br>Temperature: "
            + temperature + "°C<br>Condition: " + condition + "<br><img src='" + iconUrl + "'/></html>");
    }

    // Display 5-day weather forecast
    private void displayForecast(String response) {

    	// creating json objects
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject forecastObject = jsonObject.getAsJsonObject("forecast");
        JsonArray forecastArray = forecastObject.getAsJsonArray("forecastday");

        forecastPanel.removeAll(); // Clear old forecast data
       // main info loop
        // this gives date,day and the required information.
        for (int i = 0; i < forecastArray.size(); i++) {
            JsonObject dayForecast = forecastArray.get(i).getAsJsonObject();
            //fetch date
            String date = dayForecast.get("date").getAsString();
            //fetch day
            JsonObject day = dayForecast.getAsJsonObject("day");

            //trying to get min and max temperature in celcius format
            double maxTemp = day.get("maxtemp_c").getAsDouble();
            double minTemp = day.get("mintemp_c").getAsDouble();
            String condition = day.getAsJsonObject("condition").get("text").getAsString();

          //not sure if the image will be displayed or not...lets try
            String iconUrl = "https:" + day.getAsJsonObject("condition").get("icon").getAsString();


            JLabel forecastLabel = new JLabel("<html>" + date + " - Max: " + maxTemp + "°C, Min: " + minTemp
                    + "°C, Condition: " + condition + "<br><img src='" + iconUrl + "'/></html>");
            //forecast for 5 days.

            forecastPanel.add(forecastLabel);
        }
// not sure if this works or not
        forecastPanel.revalidate(); // Refresh the panel with new data
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                weather_1 a1=new weather_1();
                //making my swing visible
                a1.setVisible(true);
            }
        });
    }
}
