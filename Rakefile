COLORS = {
  black: 30,
  red: 31,
  green: 32,
  yellow: 33,
  blue: 34,
  purple: 35,
  cyan: 36,
  white: 37
}

def say(message, options={})
  color = COLORS[(options[:color] || :white)]
  bold = !!options[:bold] ? '1' : '0'

  puts "\033[#{bold};#{color}m#{message}\033[00m"
end

namespace :clean do
  desc 'remove idea generated files'
  task :idea do
    say('Cleaning idea generated files...', color: :green)
    Dir.glob('./**/*.{iws,iml,ipr}').each { |f| rm(f) }
  end

  desc 'remove generated files in sdk project'
  task :sdk do
    say('Cleaning sdk project...', color: :green)
    rm_rf('./sdk/target')
    rm_rf('./sdk/build')
  end

  desc 'clear the gradle cache'
  task :gradle do
    say('Cleaning gradle cache...', color: :green)
    rm_rf('./.gradle')
  end

  desc 'clean only the projects'
  task :projects => [:sdk]

  desc 'clean everything'
  task :all => [:idea, :gradle, :projects]
end
