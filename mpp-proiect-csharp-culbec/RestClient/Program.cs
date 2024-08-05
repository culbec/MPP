// See https://aka.ms/new-console-template for more information

using System.Net.Http.Headers;
using System.Net.Http.Json;
using Newtonsoft.Json;

namespace RestClient
{
    public class Race
    {
        [JsonProperty("id")] public int? Id { get; set; }
        [JsonProperty("engineCapacity")] public int EngineCapacity { get; set; }
        [JsonProperty("noParticipants")] public int NoParticipants { get; set; }

        public override string ToString()
        {
            return $"[Race: Id={Id}, EngineCapacity={EngineCapacity}, NoParticipants={NoParticipants}]";
        }
    }

    private static async Task<Race[]> GetAllRacesAsync(string path)
    {
        Race[] result = [];
        HttpResponseMessage response = await Client.GetAsync(path);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<Race[]>(json) ?? [];

        return result;
    }

    private static async Task<Race?> GetRaceAsync(string path)
    {
        Race? result = null;
        HttpResponseMessage response = await Client.GetAsync(path);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<Race>(json);

        return result;
    }

    private static async Task<Race?> CreateRaceAsync(string path, Race race)
    {
        Race? result = null;
        HttpResponseMessage response = await Client.PostAsJsonAsync(path, race);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<Race>(json);

        return result;
    }

    private static async Task<Race?> UpdateRaceAsync(string path, Race race)
    {
        Race? result = null;
        HttpResponseMessage response = await Client.PutAsJsonAsync(path, race);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<Race>(json);
        return result;
    }

    private static async Task<Race?> DeleteRaceAsync(string path, int? id)
    {
        Race? result = null;
        HttpResponseMessage response = await Client.DeleteAsync($"{path}/{id}");

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<Race>(json);
        return result;
    }

    internal class LoggingHandler(HttpMessageHandler innerHandler) : DelegatingHandler(innerHandler)
    {
        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,
            CancellationToken cancellationToken)
        {
            Console.WriteLine("Request: " + request);
            if (request.Content != null)
            {
                Console.WriteLine("Request Content: " + await request.Content.ReadAsStringAsync(cancellationToken));
            }

            Console.WriteLine();

            var response = await base.SendAsync(request, cancellationToken);
            Console.WriteLine("Response: " + response);
            Console.WriteLine("Response Content: " + await response.Content.ReadAsStringAsync(cancellationToken));
            Console.WriteLine();

            return response;
        }
    }


    internal static class MainClass
    {
        private static readonly HttpClient Client = new(new LoggingHandler(new HttpClientHandler()));
        private const string BaseUrl = "http://localhost:8080/guess_game";

        private static async Task<string?> GetTextAsync(string path)
        {
            string? result = null;
            HttpResponseMessage response = await Client.GetAsync(path);

            if (!response.IsSuccessStatusCode) return result;

            result = await response.Content.ReadAsStringAsync();
            return result;
        }

        private static async Task RunAsync()
        {
            // Create a race.
            Race race = new Race
            {
                EngineCapacity = 9999
            };
            var raceSaved = await CreateRaceAsync(BaseUrl, race);
            Console.WriteLine("Saved race: {0}\n", raceSaved);

            // Get all races.
            var allRaces = await GetAllRacesAsync(BaseUrl);
            Console.WriteLine("All races: {0}", allRaces);

            // Updating the race.
            raceSaved!.EngineCapacity = 2000;
            var raceUpdated = await UpdateRaceAsync(BaseUrl, raceSaved);
            Console.WriteLine("Updated race: {0}", raceUpdated);

            // Getting the update race.
            var raceUpdatedGet = await GetRaceAsync($"{BaseUrl}/{raceUpdated!.Id}");
            Console.WriteLine("Updated race get: {0}", raceUpdatedGet);

            // Delete race.
            var raceDeleted = await DeleteRaceAsync(BaseUrl, raceSaved.Id);
            Console.WriteLine("Deleted race: {0}\n", raceDeleted);
        }

        public static void Main()
        {
            RunAsync().GetAwaiter().GetResult();
        }
    }
}