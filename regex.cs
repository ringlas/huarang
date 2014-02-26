using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Text.RegularExpressions;

namespace ConsoleApplication3
{
    class Program
    {
        static void Main(string[] args)
        { 

        }

        static void ProcessText()
        {
            var text = File.ReadAllText(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + "\\input.txt");
            text = Regex.Replace(text, @"(?:\r\n|\r(?!\n)|(?!<\r)\n){2,}", "\n");
            var parts = (new Regex(@"\n+\d+\n+")).Split(text);
            var items = new List<string>();
            for (int i = 1; i < parts.Length; i++)
            {
                var p = Regex.Replace(parts[i], @"(?:\r\n|\r(?!\n)|(?!<\r)\n){2,}", "\n");
                items.Add(string.Format("INSERT INTO episode (\"text\", \"number\") VALUES (\"{0}\", {1});", p, i));

                var links = Regex.Split(p, @"на (\d+)");
            }

            File.WriteAllLines(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + "\\output.sql", items);
            //insert into episode ("text", "number") values ("след доста дълъг текст тук", "1")
        }

        static void ProcessHtml()
        {
            var text = File.ReadAllText(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + "\\input.html");
            //text = Regex.Replace(text, @"(?:\r\n|\r(?!\n)|(?!<\r)\n){2,}", "\n");
            var parts = (new Regex(@">\d+</P>")).Split(text);
            var items = new List<string>();
            for (int i = 1; i < parts.Length; i++)
            {
                var matches = Regex.Matches(parts[i], @"(\d+)\s*</SPAN></A>");
                foreach (Match m in matches)
                {
                    var goTo = m.Groups[1].Value;
                    items.Add(string.Format("INSERT INTO episode_links (\"episode_number\", \"go_to_episode_number\", \"link_text\") VALUES ({0}, {1}, {1});", i, goTo));
                }
            }

            File.WriteAllLines(Environment.GetFolderPath(Environment.SpecialFolder.Desktop) + "\\output_links.sql", items);
        }
    }
}
