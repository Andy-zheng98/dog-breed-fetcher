package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        Request req = new Request.Builder().url(url).build();
        try (Response response = client.newCall(req).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException(breed);
            }
            String jsonString = response.body().string();
            JSONObject json = new JSONObject(jsonString);
            String status = json.getString("status");
            if (!"success".equals(status)) {
                throw new BreedNotFoundException(breed);
            }
            JSONArray messageArray = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < messageArray.length(); i++) {
                String item = messageArray.optString(i, null);
                if (item != null) subBreeds.add(item);
            }
            return subBreeds;
        } catch (IOException | BreedNotFoundException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}