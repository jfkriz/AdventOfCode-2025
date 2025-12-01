#!/bin/sh
proj_dir=$(dirname $0)/..

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
if [ -d $day_dir ]; then
  echo "Directory ${day_dir} already exists..."
  echo "If you want to remove it and start over, use this command:"
  echo "  rm -rf ${day_dir}"
  exit 1
fi

aoc_day=$(echo $day | sed -e 's/^0//')
aoc_year=$(date +'%Y')
aoc_day_base_url=https://adventofcode.com/$aoc_year/day/$aoc_day

echo "- Copying template to new day for day number $day..."
cp -R $src_dir/dayNN $day_dir \
  || (echo "Could not copy template" && exit 1)
echo "  Done!"
echo

echo "- Retrieving title of the day's problem..."
aoc_title=$(curl --silent --fail-with-body $aoc_day_base_url | \
  grep 'article.*' | head -1 | cut -f2 -d':' | cut -f1 -d'-' | sed -e 's/^[ \t]*//;s/[ \t]*$//' | tr '"' "\"") || \
  aoc_title="Description"
echo "  Done!"
echo

echo "- Updating template for day number $day..."
perl -spi -e "s/package dayNN/package day$day/;" \
  -e "s/Day NN/Day $day/;" \
  -e "s/Description/$aoc_title/;" $day_dir/TodayTest.kt \
  || (echo "Could not update files" && exit 1)
perl -spi -e "s/Description/$aoc_title/;" $day_dir/TodayTest.kt \
  || (echo "Could not update description in files" && exit 1)
echo "  Done!"
echo

echo "- Downloading input for day number $day..."
curl --silent --fail-with-body -H "Cookie: session=$AOC_SESSION" \
  $aoc_day_base_url/input -o $day_dir/input.txt \
  || (echo "Could not download input file" && exit 1)
echo "  Done!"
echo

echo "- Starting browser for today's challenges..."
cmd.exe /C start $aoc_day_base_url > /dev/null 2>&1
echo "  Done!"
echo

cat << EOF
*** $aoc_title ***

Initial code is created for Day $aoc_day.

Be sure to refresh your IDE so you can see the new files, and may the odds be ever in your favor!
EOF

exit 0
