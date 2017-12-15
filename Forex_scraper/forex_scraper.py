from bs4 import BeautifulSoup as soup
from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException

# create and open a file to write


# Create driver
driver = webdriver.Firefox() # Tried using Chrome but it keeps crashing

# Get page
driver.get("http://www.forex.pk/foreign-exchange-rate.htm")
# Feed the source to BeautifulSoup
forex_soup = soup(driver.page_source,"html.parser")

timestamp = forex_soup.findAll("form",{"action":"intl-rates.php","method":"post"})[0].find_all("span",{"class":"normaltext"})[0].text.replace(",","|")
ind1 = timestamp.find("|")+1
ind2 = timestamp[ind1+1:].find("|")+ind1+1
print(ind1," ",ind2)
filename = timestamp[ind1:ind2].replace(" ","")+"_forex_data.csv"
f = open(filename,"w")
# write timestamp
f.write(timestamp+"\n")

# write headers
headers = "base_currency_complete_name,base_currency,other_currency,other_per_base\n"
f.write(headers)
# base_currency_complete_name includes the full name of the currency as well as code within brackets
# base_currency is the code of the base currency
# other_currency is code of the other currency
# other_per_base is the exchange rate other_currency/base_currency

possible_base_curr = forex_soup.findAll("form",{"action":"intl-rates.php","method":"post"})[0].table.find_all('tr')[3].find_all('td')[1].find_all("option")
# for each currency in possible 78 base currencies
for n in range(0,79):
    # get dropdown menu to select base currency
    temp_dropdown = driver.find_elements_by_xpath("//select[@name='currid'][@id='currid']")
    base_dropdown = Select(temp_dropdown[2])
    # note: len(base_dropdown)=79 (so 0 to 78 and # 8 is to be ignored)
    if n != 8:
        # print("n=",n)
        # select currency as base
        base_dropdown.options[n].click()
        # print("selected 0th")

        # click submit
        driver.find_element_by_id('btnShow').click()
        # print("clicked submit")

        base_currency_complete_name = possible_base_curr[n].text
        base_currency = base_currency_complete_name[base_currency_complete_name.find("(")+1:base_currency_complete_name.find(")")]

        forex_soup = soup(driver.page_source,"html.parser")

        # store exchange rate data for the current base currency
        temps1 = forex_soup.findAll("td",{"align":"center"})[12:44]
        temps2 = forex_soup.findAll("td",{"align":"center"})[53:333]
        temps = temps1+temps2
        i = 0
        for temp in temps:
            # print(i)
            if i%4==0:
                # write base currency name and code, and other currency code
                other_currency = temp.text
                f.write(base_currency_complete_name+","+base_currency+","+other_currency+",")
                # print(base_currency_complete_name+","+base_currency+","+other_currency+",") #
            elif i%4==1:
                # write other_per_base
                other_per_base = temp.text.lstrip()
                # print(other_per_base," length=",len(other_per_base))
                f.write(other_per_base+"\n")
                # print('Units per currency: ',temp.text)
            i = i+1
        print(base_currency_complete_name," DONE")

    n=n+1


# close file
f.close()
# close webdriver
driver.close()
print("Scraping complete.")

