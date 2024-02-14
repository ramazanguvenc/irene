# Telegram Bot

Telegram bot: [@k_irene_bot](https://t.me/k_irene_bot)

## Commands

- `/p btc`: Returns the current price (/p btc -- /p eth).
- `/sub daily`: Subscribe to daily reports and/or alerts. (/sub daily -- /sub vibe)
- `/test`: Get a report.
- `/video [url]`: Downloads a video from a given link on Twitter.
- `/crypto`: Posts prices of added cryptocurrencies.
- `/add btc`: Adds a cryptocurrency to see it in the `/crypto` command (/add btc).
- `/chat [input]`: Runs on Gemini API. Don't abuse it!

## Installation

1. Clone the repositories:
   - [irene](https://github.com/ramazanguvenc/irene.git)
   - [sunmi](https://github.com/ramazanguvenc/sunmi.git)

2. Install ffmpeg, yt-dlp

2. Create a `config.properties` file and define the following variables:

   ```properties
   token = 
   twitter_download_go_path = 
   twitter_download_output_path = 
   db_username = 
   db_password = 
   db_url = 
   env = 

## Usage

To launch the bot, execute the `deploy.sh` script on your server, ensuring that you have previously set up the `config.properties` files and downloaded the `sunmi` project.

```bash
./deploy.sh
