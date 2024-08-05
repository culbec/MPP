using System.Net.Sockets;
using Common.CommonUtils;
using Common.Exceptions;
using log4net;
using Network.Workers;

namespace Network.NetworkUtils;

public class ProtobufServer : AbstractConcurrentServer
{
    private static readonly ILog Log = LogManager.GetLogger(typeof(ProtobufServer));
    private readonly IService _server;
    public ProtobufServer(string host, int port, IService server) : base(host, port)
    {
        _server = server;
    }

    protected override Thread CreateWorker(TcpClient client)
    {
        Log.Info("Creating a new worker...");
        ProtobufWorker worker;

        try
        {
            worker = new ProtobufWorker(_server, client);
        }
        catch (Exception e)
        {
            Log.ErrorFormat("An error occurred: {0}", e.Message);
            throw new AppException(e.Message);
        }

        Log.Info("Worker created.");
        return new Thread(worker.Run);
    }
}