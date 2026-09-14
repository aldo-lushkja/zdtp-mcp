const vscode = require('vscode');

function activate(context) {
    console.log('[zdtp-mcp] Targetprocess MCP Extension activated.');

    let disposable = vscode.commands.registerCommand('zdtp.status', function () {
        const config = vscode.workspace.getConfiguration('zdtp');
        const url = config.get('tpUrl');
        const token = config.get('tpToken');
        const useDocker = config.get('useDocker');

        if (!url || !token) {
            vscode.window.showWarningMessage('Targetprocess MCP: TP_URL o TP_TOKEN non ancora configurati nelle impostazioni.');
        } else {
            vscode.window.showInformationMessage(`Targetprocess MCP attivo su ${url} (Modalità: ${useDocker ? 'Docker' : 'Java JAR'})`);
        }
    });

    context.subscriptions.push(disposable);
}

function deactivate() {}

module.exports = {
    activate,
    deactivate
};
