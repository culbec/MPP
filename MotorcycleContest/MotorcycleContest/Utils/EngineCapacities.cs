namespace MotorcycleContest;

public class EngineCapacities
{
    public static readonly Dictionary<string, int> ENGINE_CAPACITIES
        = new()
        {
            { "125mc", 125 },
            { "250mc", 250 },
            { "500mc", 500 },
            { "700mc", 700 },
            { "1000mc", 1000 },
            { "1500mc", 1500 },
            { "2000mc", 2000 },
            { "2500mc", 2500 }
        };
}