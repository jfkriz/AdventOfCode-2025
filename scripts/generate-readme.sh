#!/bin/sh
proj_dir=$(dirname $0)/..

which html2markdown > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Please install html2markdown, you can probably use a command like:"
  echo "sudo apt install python3-html2text"
  exit 1
fi

if [ ! -f $proj_dir/.env ]; then
  echo "Please create a .env file in the project root (you can copy the example.env file)."
  echo "This file should contain an AOC_SESSION variable, with your AoC session cookie."
  exit 1
fi

. $proj_dir/.env

if [ -z "$AOC_SESSION" ]; then
  echo "Please make sure to set the AOC_SESSION variable in your $proj_dir/.env file"
  exit 1
fi

default_day=$(date +'%d')
day=${1:-${default_day}}
#day=$(printf "%02d" $day)

if [ $day -lt 1 ] || [ $day -gt 25 ] || [ $day -gt $default_day ]; then
  echo "You can only run this for days 1-25 in December, and you can't request a date in the future."
  exit 1
fi

src_dir="${proj_dir}/src/test/kotlin"
day_dir="${src_dir}/day$day"
if [ ! -d $day_dir ]; then
  echo "Directory ${day_dir} does not exist..."
  exit 1
fi

aoc_day=$(echo $day | sed -e 's/^0//')
aoc_year=$(date +'%Y')
aoc_day_base_url=https://adventofcode.com/$aoc_year/day/$aoc_day

curl --silent --fail-with-body -H "Cookie: session=$AOC_SESSION" $aoc_day_base_url -o $day_dir/README.html
if [ $? -ne 0 ]; then
  echo "Could not download html for readme"
  exit 1
fi

aoc_day_base_url=$(echo "$aoc_day_base_url" | sed -e 's/\//\\\//g')

html2markdown $day_dir/README.html | \
  awk '/^## \\--- Day/,/^If you still want to see it/{ print }' | \
  sed -e 's/^## \\--- Day \(.*\) ---.*/# [Day \1]('$aoc_day_base_url')\n## Part One/' \
      -e 's/^## \\--- Part Two ---.*/## Part Two/' \
      -e 's/\](\//](https:\/\/adventofcode.com\//' \
      -e "s/\](\([0-9]\)/](https:\/\/adventofcode.com\/$aoc_year\/day\/\1/" \
> $day_dir/README.md && rm $day_dir/README.html
