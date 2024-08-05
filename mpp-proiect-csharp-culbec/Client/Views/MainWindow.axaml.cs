using System;
using System.ComponentModel;
using Avalonia.Controls;
using GUI.ViewModels;

namespace GUI.Views;

public partial class MainWindow : Window
{
    public MainWindow()
    {
        InitializeComponent();
        DataContextChanged += OnDataContextChanged;
        Closing += OnClosing;
    }

    private void OnDataContextChanged(object? sender, EventArgs e)
    {
        if (DataContext is MainWindowViewModel viewModel)
        {
            viewModel.LogoutRequested -= Close;
            viewModel.LogoutRequested += Close;

            viewModel.ClosedConnection -= Close;
            viewModel.ClosedConnection += Close;
        }
    }

    private void OnClosing(object? sender, EventArgs e)
    {
        if (DataContext is MainWindowViewModel viewModel)
        {
            viewModel.LogoutAction();
        }
    }
}