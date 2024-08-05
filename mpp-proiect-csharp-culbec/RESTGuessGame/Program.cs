// See https://aka.ms/new-console-template for more information

using System.Net.Http.Headers;
using System.Net.Http.Json;
using Newtonsoft.Json;

public class GameConfiguration
{
    [JsonProperty("id")] public int? Id { get; set; }
    [JsonProperty("lettersProposed")] public string LettersProposed { get; set; }

    public override string ToString()
    {
        return $"[GameConfiguration: Id={Id}, LettersProposed={LettersProposed}]";
    }
}

public class FinishedGameResponse
{
    [JsonProperty("playerAlias")] public string PlayerAlias { get; set; }
    [JsonProperty("points")] public int Points { get; set; }
    [JsonProperty("positionsPlayer")] public string PositionsPlayer { get; set; }
    [JsonProperty("positionsServer")] public string PositionsServer { get; set; }

    public override string ToString()
    {
        return $"[FinishedGameResponse: PlayerAlias={PlayerAlias}, Points={Points}, PositionsPlayer={PositionsPlayer}, PositionsServer={PositionsServer}]";
    }
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

    private static async Task<FinishedGameResponse[]> GetFinishedGamesOfPlayerAsync(string path)
    {
        FinishedGameResponse[] result = [];
        HttpResponseMessage response = await Client.GetAsync(path);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<FinishedGameResponse[]>(json) ?? [];

        return result;
    }

    private static async Task<GameConfiguration?> UpdateGameConfigurationAsync(string path,
        GameConfiguration gameConfiguration)
    {
        GameConfiguration? result = null;
        HttpResponseMessage response = await Client.PutAsJsonAsync(path, gameConfiguration);

        if (!response.IsSuccessStatusCode) return result;

        var json = await response.Content.ReadAsStringAsync();
        result = JsonConvert.DeserializeObject<GameConfiguration>(json);
        return result;
    }

    private static async Task RunAsync()
    {
        Client.BaseAddress = new Uri(BaseUrl);
        Client.DefaultRequestHeaders.Accept.Clear();
        Client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

        // Get all finished games of a player.
        var finishedGames = await GetFinishedGamesOfPlayerAsync($"{BaseUrl}/game/test");
        Console.WriteLine("Finished games: {0}", finishedGames);

        // Update a game configuration.
        GameConfiguration gameConfiguration = new GameConfiguration
        {
            Id = 1,
            LettersProposed = "A,1 B,2 C,3 B,4"
        };

        var gameUpdated = await UpdateGameConfigurationAsync($"{BaseUrl}/game_configuration", gameConfiguration);
        Console.WriteLine("Updated game: {0}\n", gameUpdated);
    }

    public static void Main()
    {
        RunAsync().GetAwaiter().GetResult();
    }
}