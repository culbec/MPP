using System;
using Avalonia.Controls;
using GUI.ViewModels;

namespace GUI.Views;

public partial class LoginWindow : Window
{
    public LoginWindow()
    {
        InitializeComponent();
        DataContextChanged += OnDataContextChanged;
    }

    private void OnDataContextChanged(object? sender, EventArgs e)
    {
        if (DataContext is LoginWindowViewModel viewModel)
        {
            viewModel.HideRequested -= Hide;
            viewModel.HideRequested += Hide;

            viewModel.ShowRequested -= Show;
            viewModel.ShowRequested += Show;
        }
    }
}