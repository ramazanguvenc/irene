
# Find the session ID for the specified screen name
SESSION_ID=$(screen -ls | grep "$SCREEN_NAME" | awk '{print $1}' | cut -d'.' -f1)

# Check if the session ID is not empty (screen session found)
if [ -n "$SESSION_ID" ]; then
    # Send the kill command to the screen session
    screen -S "$SESSION_ID" -X quit
    echo "Screen session $SCREEN_NAME killed successfully."
else
    echo "No screen session found with the name $SCREEN_NAME."
fi

